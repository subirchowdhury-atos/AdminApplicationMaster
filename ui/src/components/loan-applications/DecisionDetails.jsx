/**
 * Decision Details Component
 * Replaces Rails _decision_details.html.erb partial
 * Displays financing options from loan application decision
 */
function DecisionDetails({ applicationDecision }) {
  if (!applicationDecision || !applicationDecision.parsedResponse) {
    return null;
  }

  const fundingOptions = applicationDecision.parsedResponse.funding_options || [];

  if (fundingOptions.length === 0) {
    return null;
  }

  return (
    <div className="row widget-box">
      <div className="widget-header green">
        <h4 className="widget-title">Available Financing Options</h4>
      </div>
      <div className="col-xs-12">
        <div className="widget-body">
          <div className="widget-main">
            <table className="table table-bordered table-hover">
              <thead>
                <tr>
                  <th>Years</th>
                  <th>Interest Rate</th>
                  <th>EMI</th>
                </tr>
              </thead>
              <tbody>
                {fundingOptions.map((option, index) => (
                  <tr key={index}>
                    <td>{option.years}</td>
                    <td>{option.interest_rate}%</td>
                    <td>${option.emi}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DecisionDetails;