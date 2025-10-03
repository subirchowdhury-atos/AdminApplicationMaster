import { formatDate, formatCurrency } from '../../utils/helpers';

/**
 * Personal Details Display Component (Read-Only)
 * Replaces Rails _personal_details.html.erb partial
 * Shows loan application details in read-only format
 */
function PersonalDetailsDisplay({ loanApplication }) {
  if (!loanApplication) {
    return null;
  }

  return (
    <div className="row widget-box">
      <div className="widget-header">
        <h4 className="widget-title">Personal Details</h4>
      </div>
      <div className="col-xs-12">
        <div className="widget-body">
          <div className="widget-main">
            <form className="form-horizontal">
              
              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">First Name</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    value={loanApplication.firstName || ''}
                    className="col-xs-10 col-sm-5"
                    readOnly
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Last Name</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    value={loanApplication.lastName || ''}
                    className="col-xs-10 col-sm-5"
                    readOnly
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Date of Birth</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    value={loanApplication.dateOfBirth || ''}
                    className="col-xs-10 col-sm-5"
                    readOnly
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">SSN</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    value={loanApplication.ssn || ''}
                    className="col-xs-10 col-sm-5"
                    readOnly
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Email</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    value={loanApplication.email || ''}
                    className="col-xs-10 col-sm-5"
                    readOnly
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Phone</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    value={loanApplication.phone || ''}
                    className="col-xs-10 col-sm-5"
                    readOnly
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Annual Income</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    value={formatCurrency(loanApplication.income)}
                    className="col-xs-10 col-sm-5"
                    readOnly
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Income Type</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    value={loanApplication.incomeType || ''}
                    className="col-xs-10 col-sm-5"
                    readOnly
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Required Loan Amount</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    value={formatCurrency(loanApplication.requestedLoanAmount)}
                    className="col-xs-10 col-sm-5"
                    readOnly
                  />
                </div>
              </div>

            </form>
          </div>
        </div>
      </div>
    </div>
  );
}

export default PersonalDetailsDisplay;