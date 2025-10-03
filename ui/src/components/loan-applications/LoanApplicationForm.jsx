import { useState } from 'react';

/**
 * Loan Application Form Component
 * Replaces Rails _form.html.erb partial
 */
function LoanApplicationForm({ initialData = {}, addressId = null, onSubmit, isEditing = false }) {
  const [formData, setFormData] = useState({
    firstName: initialData.firstName || '',
    lastName: initialData.lastName || '',
    dateOfBirth: initialData.dateOfBirth || '',
    ssn: initialData.ssn || '',
    email: initialData.email || '',
    phone: initialData.phone || '',
    income: initialData.income || '',
    incomeType: initialData.incomeType || '',
    requestedLoanAmount: initialData.requestedLoanAmount || '',
    addressId: initialData.addressId || addressId || ''
  });

  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear error for this field when user starts typing
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: null }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.firstName.trim()) newErrors.firstName = 'First name is required';
    if (!formData.lastName.trim()) newErrors.lastName = 'Last name is required';
    if (!formData.dateOfBirth) newErrors.dateOfBirth = 'Date of birth is required';
    if (!formData.ssn.trim()) newErrors.ssn = 'SSN is required';
    if (!formData.email.trim()) newErrors.email = 'Email is required';
    if (!formData.phone.trim()) newErrors.phone = 'Phone is required';
    if (!formData.income) newErrors.income = 'Annual income is required';
    if (!formData.incomeType) newErrors.incomeType = 'Income type is required';
    if (!formData.requestedLoanAmount) newErrors.requestedLoanAmount = 'Loan amount is required';

    // Email validation
    if (formData.email && !/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Invalid email format';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      await onSubmit(formData);
    } catch (error) {
      console.error('Form submission error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="row widget-box">
      <div className="widget-header">
        <h4 className="widget-title">Personal Details</h4>
      </div>
      <div className="col-xs-12">
        <div className="widget-body">
          <div className="widget-main">
            <form className="form-horizontal" onSubmit={handleSubmit}>
              
              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">First Name*</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleChange}
                    className="col-xs-10 col-sm-5"
                    placeholder="First Name"
                  />
                  {errors.firstName && <span className="help-block text-danger">{errors.firstName}</span>}
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Last Name*</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleChange}
                    className="col-xs-10 col-sm-5"
                    placeholder="Last Name"
                  />
                  {errors.lastName && <span className="help-block text-danger">{errors.lastName}</span>}
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Date of Birth*</label>
                <div className="col-sm-9">
                  <input
                    type="date"
                    name="dateOfBirth"
                    value={formData.dateOfBirth}
                    onChange={handleChange}
                    className="col-xs-10 col-sm-5"
                  />
                  {errors.dateOfBirth && <span className="help-block text-danger">{errors.dateOfBirth}</span>}
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">SSN*</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    name="ssn"
                    value={formData.ssn}
                    onChange={handleChange}
                    className="col-xs-10 col-sm-5"
                    placeholder="SSN"
                  />
                  {errors.ssn && <span className="help-block text-danger">{errors.ssn}</span>}
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Email*</label>
                <div className="col-sm-9">
                  <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    className="col-xs-10 col-sm-5"
                    placeholder="Email"
                  />
                  {errors.email && <span className="help-block text-danger">{errors.email}</span>}
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Phone*</label>
                <div className="col-sm-9">
                  <input
                    type="tel"
                    name="phone"
                    value={formData.phone}
                    onChange={handleChange}
                    className="col-xs-10 col-sm-5"
                    placeholder="Phone"
                  />
                  {errors.phone && <span className="help-block text-danger">{errors.phone}</span>}
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Annual Income*</label>
                <div className="col-sm-9">
                  <input
                    type="number"
                    name="income"
                    value={formData.income}
                    onChange={handleChange}
                    className="col-xs-10 col-sm-5"
                    placeholder="Annual Income"
                    min="0"
                    step="any"
                  />
                  {errors.income && <span className="help-block text-danger">{errors.income}</span>}
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Income Type*</label>
                <div className="col-sm-9">
                  <select
                    name="incomeType"
                    value={formData.incomeType}
                    onChange={handleChange}
                    className="col-xs-10 col-sm-5"
                  >
                    <option value="">Select</option>
                    <option value="Salary">Salary</option>
                    <option value="Self-Employed">Self-Employed</option>
                    <option value="Rental Income">Rental Income</option>
                  </select>
                  {errors.incomeType && <span className="help-block text-danger">{errors.incomeType}</span>}
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Required Loan Amount*</label>
                <div className="col-sm-9">
                  <input
                    type="number"
                    name="requestedLoanAmount"
                    value={formData.requestedLoanAmount}
                    onChange={handleChange}
                    className="col-xs-10 col-sm-5"
                    placeholder="Amount"
                    min="0"
                    step="any"
                  />
                  {errors.requestedLoanAmount && <span className="help-block text-danger">{errors.requestedLoanAmount}</span>}
                </div>
              </div>

              {formData.addressId && (
                <input type="hidden" name="addressId" value={formData.addressId} />
              )}

              <div className="space-4"></div>

              <div className="clearfix form-actions">
                <div className="col-md-offset-3 col-md-9">
                  <button 
                    type="submit" 
                    className="btn btn-info"
                    disabled={loading}
                  >
                    {loading ? 'Submitting...' : 'Submit'}
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoanApplicationForm;