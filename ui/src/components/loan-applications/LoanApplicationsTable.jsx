import { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { loanApplicationApi } from '../../api/loanApplicationApi';
import { getStatusClass, capitalize } from '../../utils/helpers';

/**
 * Loan Applications Table Component
 * Replaces Rails _table.html.erb partial
 */
function LoanApplicationsTable() {
  const [loanApplications, setLoanApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pagination, setPagination] = useState({ page: 1, totalPages: 1 });
  const [searchParams, setSearchParams] = useSearchParams();
  
  const statusFilter = searchParams.get('status_search') || '';
  const currentPage = parseInt(searchParams.get('page') || '1');

  useEffect(() => {
    fetchLoanApplications();
  }, [statusFilter, currentPage]);

  const fetchLoanApplications = async () => {
    setLoading(true);
    try {
      const params = {
        page: currentPage - 1, // Spring Boot uses 0-based pagination
        size: 20
      };
      
      if (statusFilter) {
        params.status = statusFilter;
      }

      const response = await loanApplicationApi.getAll(params);
      setLoanApplications(response.data.content || response.data);
      
      // Handle pagination if backend returns it
      if (response.data.totalPages) {
        setPagination({
          page: response.data.number + 1,
          totalPages: response.data.totalPages
        });
      }
    } catch (error) {
      console.error('Error fetching loan applications:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusSearch = (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const status = formData.get('status_search');
    
    const newParams = {};
    if (status) {
      newParams.status_search = status;
    }
    newParams.page = '1'; // Reset to first page on new search
    
    setSearchParams(newParams);
  };

  const handlePageChange = (page) => {
    const newParams = Object.fromEntries(searchParams);
    newParams.page = page.toString();
    setSearchParams(newParams);
  };

  if (loading) {
    return <div className="text-center p-5">Loading...</div>;
  }

  return (
    <div>
      <h3>Loan Applications</h3>
      <br />
      
      <div className="row">
        <div className="col-xs-3 no-padding-left">
          <form className="form-search" onSubmit={handleStatusSearch}>
            <div className="input-group">
              <select 
                name="status_search" 
                className="form-control search-query"
                defaultValue={statusFilter}
              >
                <option value="">Select</option>
                <option value="pending">Pending</option>
                <option value="approved">Approved</option>
                <option value="rejected">Rejected</option>
              </select>
              <span className="input-group-btn">
                <button type="submit" className="btn btn-info btn-sm">
                  <i className="fa fa-search"></i> Search
                </button>
              </span>
            </div>
          </form>
        </div>
        
        <Link 
          to="/loan-applications/new" 
          className="btn btn-info btn-sm pull-right"
        >
          Create New Application
        </Link>
      </div>

      <table className="table table-bordered table-hover" id="simple-table">
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
              <td colSpan="7" className="text-center">No loan applications found</td>
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
                  <span className={`label label-sm label-${getStatusClass(application.status)} arrowed-in`}>
                    {capitalize(application.status)}
                  </span>
                </td>
                <td>
                  <div className="hidden-sm hidden-xs btn-group">
                    <Link 
                      to={`/loan-applications/${application.id}`}
                      className="btn btn-xs btn-success"
                    >
                      <span className="ace-icon fa fa-check bigger-120">Show</span>
                    </Link>
                  </div>
                  <div className="hidden-sm hidden-xs btn-group">
                    <Link 
                      to={`/loan-applications/${application.id}/edit`}
                      className="btn btn-xs btn-info"
                    >
                      <span className="ace-icon fa fa-pencil bigger-120">Edit</span>
                    </Link>
                  </div>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {/* Pagination */}
      {pagination.totalPages > 1 && (
        <nav aria-label="Page navigation">
          <ul className="pagination">
            <li className={pagination.page === 1 ? 'disabled' : ''}>
              <a 
                href="#" 
                onClick={(e) => { e.preventDefault(); handlePageChange(pagination.page - 1); }}
              >
                Previous
              </a>
            </li>
            
            {[...Array(pagination.totalPages)].map((_, index) => (
              <li key={index + 1} className={pagination.page === index + 1 ? 'active' : ''}>
                <a 
                  href="#" 
                  onClick={(e) => { e.preventDefault(); handlePageChange(index + 1); }}
                >
                  {index + 1}
                </a>
              </li>
            ))}
            
            <li className={pagination.page === pagination.totalPages ? 'disabled' : ''}>
              <a 
                href="#" 
                onClick={(e) => { e.preventDefault(); handlePageChange(pagination.page + 1); }}
              >
                Next
              </a>
            </li>
          </ul>
        </nav>
      )}
    </div>
  );
}

export default LoanApplicationsTable;