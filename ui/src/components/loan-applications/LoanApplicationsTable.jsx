import { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { loanApplicationApi } from '../../api/loanApplicationApi';
import { getStatusClass, capitalize } from '../../utils/helpers';
import '../../styles/LoanApplicationsPage.css';

function LoanApplicationsTable() {
  const [loanApplications, setLoanApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchParams, setSearchParams] = useSearchParams();
  
  const statusFilter = searchParams.get('status_search') || '';

  useEffect(() => {
    fetchLoanApplications();
  }, [statusFilter]);

  const fetchLoanApplications = async () => {
    setLoading(true);
    try {
      const params = {
        page: 0,
        size: 20
      };
      
      if (statusFilter) {
        params.status = statusFilter;
      }
      
      const response = await loanApplicationApi.getAll(params);
      
      // Handle paginated response
      if (response.data.content) {
        setLoanApplications(response.data.content);
        // Optional: store pagination info for later use
        // setPagination({ total: response.data.totalElements, pages: response.data.totalPages });
      } else {
        setLoanApplications(response.data);
      }
    } catch (error) {
      console.error('Error fetching loan applications:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusSearch = (e) => {
    const status = e.target.value;
    if (status) {
      setSearchParams({ status_search: status });
    } else {
      setSearchParams({});
    }
  };

  if (loading) {
    return <div className="loading-container">Loading loan applications...</div>;
  }

  return (
    <div className="loan-applications-page">
      <div className="page-header">
        <h1>Loan Applications</h1>
        <Link to="/loan-applications/new" className="create-btn">
          Create New Application
        </Link>
      </div>

      <div className="search-section">
        <div className="search-form">
          <select 
            value={statusFilter}
            onChange={handleStatusSearch}
          >
            <option value="">All Statuses</option>
            <option value="pending">Pending</option>
            <option value="approved">Approved</option>
            <option value="rejected">Rejected</option>
          </select>
        </div>
      </div>

      <div className="table-container">
        <table className="applications-table">
          <thead>
            <tr>
              <th>Application ID</th>
              <th>First Name</th>
              <th>Last Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {loanApplications.length === 0 ? (
              <tr>
                <td colSpan="7" style={{ textAlign: 'center' }}>
                  No loan applications found
                </td>
              </tr>
            ) : (
              loanApplications.map((application) => (
                <tr key={application.id}>
                  <td>{application.id}</td>
                  <td>{application.firstName}</td>
                  <td>{application.lastName}</td>
                  <td>{application.email}</td>
                  <td>{application.phone}</td>
                  <td>
                    <span className={`status-label status-${application.status.toLowerCase()}`}>
                      {capitalize(application.status)}
                    </span>
                  </td>
                  <td>
                    <div className="action-buttons">
                      <Link 
                        to={`/loan-applications/${application.id}`}
                        className="btn-action btn-show"
                      >
                        Show
                      </Link>
                      <Link 
                        to={`/loan-applications/${application.id}/edit`}
                        className="btn-action btn-edit"
                      >
                        Edit
                      </Link>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default LoanApplicationsTable;