// ========================================
// EditLoanApplicationPage.jsx
// ========================================
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { loanApplicationApi } from '../api/loanApplicationApi';
import FlashMessage from '../components/common/FlashMessage';
import LoanApplicationForm from '../components/loan-applications/LoanApplicationForm';

export function EditLoanApplicationPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loanApplication, setLoanApplication] = useState(null);
  const [loading, setLoading] = useState(true);
  const [flashMessage, setFlashMessage] = useState(null);
  const [flashType, setFlashType] = useState('info');

  useEffect(() => {
    fetchLoanApplication();
  }, [id]);

  const fetchLoanApplication = async () => {
    try {
      const response = await loanApplicationApi.getById(id);
      setLoanApplication(response.data);
    } catch (error) {
      setFlashMessage('Error loading loan application');
      setFlashType('error');
      navigate('/loan-applications');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (formData) => {
    try {
      await loanApplicationApi.update(id, formData);
      setFlashMessage('Loan application updated successfully!');
      setFlashType('success');
      navigate(`/loan-applications/${id}`);
    } catch (error) {
      setFlashMessage('Error updating loan application');
      setFlashType('error');
    }
  };

  if (loading) {
    return <div className="text-center p-5">Loading...</div>;
  }

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
        <LoanApplicationForm 
          initialData={loanApplication}
          onSubmit={handleSubmit}
          isEditing={true}
        />
      </div>
    </div>
  );
}