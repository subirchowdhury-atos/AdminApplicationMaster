import { useState } from 'react';
import { locationApi } from '../../api/locationApi';
import FlashMessage from '../common/FlashMessage';

/**
 * Address Eligibility Component
 * Replaces Rails _address_eligibility.html.erb partial
 */
function AddressEligibility({ onAddressVerified }) {
  const [address, setAddress] = useState('');
  const [loading, setLoading] = useState(false);
  const [flashMessage, setFlashMessage] = useState(null);
  const [flashType, setFlashType] = useState('info');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!address.trim()) {
      setFlashMessage('Please enter an address');
      setFlashType('warning');
      return;
    }

    setLoading(true);
    try {
      const response = await locationApi.checkAddress(address);
      
      if (response.data) {
        setFlashMessage('Address is eligible');
        setFlashType('success');
        // Pass the verified address back to parent component
        if (onAddressVerified) {
          onAddressVerified(response.data);
        }
      }
    } catch (error) {
      if (error.response?.status === 404) {
        setFlashMessage('Address not eligible');
        setFlashType('error');
      } else {
        setFlashMessage('Location service error');
        setFlashType('error');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="row widget-box">
      <div className="widget-header">
        <h4 className="widget-title">Address Details</h4>
      </div>
      <div className="col-xs-12">
        <div className="widget-body">
          <div className="widget-main">
            {flashMessage && (
              <FlashMessage
                message={flashMessage}
                type={flashType}
                duration={5000}
                onClose={() => setFlashMessage(null)}
              />
            )}
            
            <form className="form-inline" onSubmit={handleSubmit}>
              <input
                type="text"
                name="address"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                className="col-xs-5 col-sm-3"
                placeholder="Address"
                required
                disabled={loading}
              />
              <button 
                type="submit" 
                className="btn btn-info btn-sm"
                disabled={loading}
              >
                {loading ? 'Checking...' : 'Submit'}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AddressEligibility;