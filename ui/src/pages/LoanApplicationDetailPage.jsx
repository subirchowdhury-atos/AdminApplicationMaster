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
        
        <div className="page-header">
          <h1>
            Loan Application
            <small>
              <i className="ace-icon fa fa-angle-double-right"></i>
            </small>
            <button 
              onClick={handleCheckDecision}
              className="btn btn-small btn-info pull-right"
              disabled={checkingDecision}
            >
              {checkingDecision ? 'Checking...' : 'Check Decision'}
            </button>
          </h1>
        </div>
        
        <h2>
          <i className="ace-icon fa fa-home"></i>&nbsp;
          {loanApplication.address?.street}, {loanApplication.address?.state}, {loanApplication.address?.county} {loanApplication.address?.zip}
        </h2>
        
        <h3 className={`label label-lg label-${getStatusClass(loanApplication.status)} arrowed-right`}>
          Application Status: {capitalize(loanApplication.status)}
        </h3>
        
        {loanApplication.lastApplicationDecision && 
         loanApplication.lastApplicationDecision.decision === 'eligible' && (
          <div className="row widget-box">
            <DecisionDetails applicationDecision={loanApplication.lastApplicationDecision} />
          </div>
        )}
        
        <div className="row widget-box">
          <PersonalDetailsDisplay loanApplication={loanApplication} />
        </div>
      </div>
    </div>
  );
}