// ========================================
// LoanApplicationsPage.jsx (index)
// ========================================
import LoanApplicationsTable from '../components/loan-applications/LoanApplicationsTable';

function LoanApplicationsPage() {
  return (
    <div className="main-content-inner">
      <div className="page-content">
        <div className="row">
          <div className="col-xs-12">
            <LoanApplicationsTable />
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoanApplicationsPage;