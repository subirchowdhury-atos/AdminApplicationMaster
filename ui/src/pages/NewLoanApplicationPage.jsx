// ========================================
// NewLoanApplicationPage.jsx
// ========================================
import { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { loanApplicationApi } from '../api/loanApplicationApi';
import FlashMessage from '../components/common/FlashMessage';
import AddressEligibility from '../components/loan-applications/AddressEligibility';
import LoanApplicationForm from '../components/loan-applications/LoanApplicationForm';

export function NewLoanApplicationPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [address, setAddress] = useState(null);
  const [flashMessage, setFlashMessage] = useState(null);
  const [flashType, setFlashType] = useState('info');
  const addressId = searchParams.get('address_id');

  const handleAddressVerified = (verifiedAddress) => {
    setAddress(verifiedAddress);
    // Update URL with address_id
    navigate(`/loan-applications/new?address_id=${verifiedAddress.id}`, { replace: true });
  };

  const handleSubmit = async (formData) => {
    try {
      const response = await loanApplicationApi.create(formData);
      setFlashMessage('Loan application created successfully!');
      setFlashType('success');
      navigate(`/loan-applications/${response.data.id}`);
    } catch (error) {
      setFlashMessage('Error creating loan application');
      setFlashType('error');
    }
  };

  return (
    <div className="main-content-inner">
      <div className="page-content">
        {flashMessage && (
          <FlashMessage
            message={flashMessage}
            type={flashType}
            duration={5000}
            onClose={() => setFlashMessage(null)}
          />
        )}
        
        <div className="page-header">
          <h1>
            Loan Application
            <small>
              <i className="ace-icon fa fa-angle-double-right"></i>
            </small>
          </h1>
        </div>
        
        {addressId || address ? (
          <>
            {address && (
              <h2>
                <i className="ace-icon fa fa-home"></i>&nbsp;
                {`${address.street}, ${address.state}, ${address.county} ${address.zip}`}
              </h2>
            )}
            <LoanApplicationForm 
              addressId={addressId || address?.id}
              onSubmit={handleSubmit}
            />
          </>
        ) : (
          <AddressEligibility onAddressVerified={handleAddressVerified} />
        )}
      </div>
    </div>
  );
}