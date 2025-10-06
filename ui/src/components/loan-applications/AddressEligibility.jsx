import { useState } from 'react';
import { locationApi } from '../../api/locationApi';
import '../../styles/LoanApplicationForm.css';

function AddressEligibility({ onAddressVerified }) {
  const [address, setAddress] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!address.trim()) {
      setMessage({ type: 'error', text: 'Please enter an address' });
      return;
    }

    setLoading(true);
    setMessage(null);

    try {
      const response = await locationApi.checkAddress(address);
      
      if (response) {
        setMessage({ type: 'success', text: 'Address is eligible!' });
        if (onAddressVerified) {
          onAddressVerified(response);
        }
      }
    } catch (error) {
      // The error.message contains the actual message from the API
      const errorMessage = error.message || 'Unknown error';
      
      // "Address Not found" is not really an error - it's just ineligible
      if (errorMessage.includes('Address Not found') || 
          errorMessage.includes('Address not found') ||
          errorMessage.includes('not eligible')) {
        setMessage({ type: 'info', text: errorMessage });
      } else {
        // Actual service errors
        setMessage({ type: 'error', text: errorMessage });
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-section">
      <h3>Address Details</h3>
      
      {message && (
        <div className={`alert alert-${message.type}`}>
          {message.text}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="address-search-group">
          <input
            type="text"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            placeholder="Enter property address"
            disabled={loading}
          />
          <button 
            type="submit" 
            className="address-search-btn"
            disabled={loading}
          >
            {loading ? 'Checking...' : 'Submit'}
          </button>
        </div>
        <span className="help-text">
          Enter the full property address to check eligibility
        </span>
      </form>
    </div>
  );
}

export default AddressEligibility;