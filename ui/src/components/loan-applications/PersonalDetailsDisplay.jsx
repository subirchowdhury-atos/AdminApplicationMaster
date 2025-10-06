import { formatCurrency } from '../../utils/helpers';
import '../../styles/PersonalDetailsDisplay.css';

function PersonalDetailsDisplay({ loanApplication }) {
  if (!loanApplication) {
    return null;
  }

  return (
    <div className="personal-details-display">
      <div className="details-section">
        <h3>Personal Details</h3>
        
        <div className="detail-row">
          <label>First Name</label>
          <div className="detail-value">{loanApplication.firstName || '-'}</div>
        </div>

        <div className="detail-row">
          <label>Last Name</label>
          <div className="detail-value">{loanApplication.lastName || '-'}</div>
        </div>

        <div className="detail-row">
          <label>Date of Birth</label>
          <div className="detail-value">{loanApplication.dateOfBirth || '-'}</div>
        </div>

        <div className="detail-row">
          <label>SSN</label>
          <div className="detail-value">{loanApplication.ssn || '-'}</div>
        </div>

        <div className="detail-row">
          <label>Email</label>
          <div className="detail-value">{loanApplication.email || '-'}</div>
        </div>

        <div className="detail-row">
          <label>Phone</label>
          <div className="detail-value">{loanApplication.phone || '-'}</div>
        </div>

        <div className="detail-row">
          <label>Annual Income</label>
          <div className="detail-value">{formatCurrency(loanApplication.income)}</div>
        </div>

        <div className="detail-row">
          <label>Income Type</label>
          <div className="detail-value">{loanApplication.incomeType || '-'}</div>
        </div>

        <div className="detail-row">
          <label>Requested Loan Amount</label>
          <div className="detail-value">{formatCurrency(loanApplication.requestedLoanAmount)}</div>
        </div>
      </div>
    </div>
  );
}

export default PersonalDetailsDisplay;