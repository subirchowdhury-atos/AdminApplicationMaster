// ========================================
// LoanApplicationDetailPage.jsx (show)
// ========================================
import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { loanApplicationApi } from '../api/loanApplicationApi';
import FlashMessage from '../components/common/FlashMessage';
import { getStatusClass, capitalize } from '../utils/helpers';
import PersonalDetailsDisplay from '../components/loan-applications/PersonalDetailsDisplay';
import DecisionDetails from '../components/loan-applications/DecisionDetails';

export function LoanApplicationDetailPage() {
  const { id } = useParams();
  const [loanApplication, setLoanApplication] = useState(null);
  const [loading, setLoading] = useState(true);
  const [checkingDecision, setCheckingDecision] = useState(false);
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
    } finally {
      setLoading(false);
    }
  };

  const handleCheckDecision = async () => {
    setCheckingDecision(true);
    try {
      const response = await loanApplicationApi.decisionCheck(id);
      setFlashMessage('Decision check completed successfully!');
      setFlashType('success');
      // Refresh the application data to get updated decision
      await fetchLoanApplication();
    } catch (error) {
      setFlashMessage('Decision service error');
      setFlashType('error');
    } finally {
      setCheckingDecision(false);
    }
  };

  if (loading) {
    return <div className="text-center p-5">Loading...</div>;
  }

  if (!loanApplication) {
    return <div className="text-center p-5">Loan application not found</div>;
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
        
        <div className="page-header" style={{ position: 'relative' }}>
          <h1 style={{ display: 'inline-block', marginRight: '20px' }}>
            Loan Application
          </h1>
          <button 
            onClick={handleCheckDecision}
            className="btn btn-info"
            disabled={checkingDecision}
            style={{
              position: 'absolute',
              right: '0',
              top: '50%',
              transform: 'translateY(-50%)',
              padding: '10px 20px',
              fontSize: '14px',
              backgroundColor: '#5087b9',
              border: 'none',
              borderRadius: '4px',
              color: 'white',
              cursor: checkingDecision ? 'not-allowed' : 'pointer'
            }}
          >
            {checkingDecision ? 'Checking...' : 'Check Decision'}
          </button>
        </div>
        
        <div style={{ marginBottom: '20px', padding: '15px', backgroundColor: '#f8f9fa', borderRadius: '4px' }}>
          <h3 style={{ margin: '0 0 10px 0', fontSize: '18px', color: '#333' }}>
            <i className="ace-icon fa fa-home" style={{ marginRight: '8px' }}></i>
            {loanApplication.address?.street}, {loanApplication.address?.city}, {loanApplication.address?.state} {loanApplication.address?.zip}
          </h3>
          <span className={`label label-${getStatusClass(loanApplication.status)}`} style={{ fontSize: '14px', padding: '5px 12px' }}>
            Status: {capitalize(loanApplication.status)}
          </span>
        </div>
        
        {loanApplication.lastApplicationDecision && 
        loanApplication.lastApplicationDecision.decision === 'eligible' && (
          <DecisionDetails applicationDecision={loanApplication.lastApplicationDecision} />
        )}
        
        <PersonalDetailsDisplay loanApplication={loanApplication} />
      </div>
    </div>
  );
}