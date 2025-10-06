import { useState } from 'react';
import { Link } from 'react-router-dom';
import '../../styles/LoanApplicationForm.css';

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
    <div className="loan-application-form-page">
      <div className="page-header">
        <h1>{isEditing ? 'Edit Loan Application' : 'New Loan Application'}</h1>
      </div>

      <form onSubmit={handleSubmit}>
        <div className="form-section">
          <h3>Personal Information</h3>
          
          <div className="form-row">
            <div className="form-group">
              <label className="required">First Name</label>
              <input
                type="text"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
                className={errors.firstName ? 'error' : ''}
                placeholder="Enter first name"
              />
              {errors.firstName && <span className="error-message">{errors.firstName}</span>}
            </div>

            <div className="form-group">
              <label className="required">Last Name</label>
              <input
                type="text"
                name="lastName"
                value={formData.lastName}
                onChange={handleChange}
                className={errors.lastName ? 'error' : ''}
                placeholder="Enter last name"
              />
              {errors.lastName && <span className="error-message">{errors.lastName}</span>}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="required">Date of Birth</label>
              <input
                type="date"
                name="dateOfBirth"
                value={formData.dateOfBirth}
                onChange={handleChange}
                className={errors.dateOfBirth ? 'error' : ''}
              />
              {errors.dateOfBirth && <span className="error-message">{errors.dateOfBirth}</span>}
            </div>

            <div className="form-group">
              <label className="required">SSN</label>
              <input
                type="text"
                name="ssn"
                value={formData.ssn}
                onChange={handleChange}
                className={errors.ssn ? 'error' : ''}
                placeholder="XXX-XX-XXXX"
              />
              {errors.ssn && <span className="error-message">{errors.ssn}</span>}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="required">Email</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className={errors.email ? 'error' : ''}
                placeholder="email@example.com"
              />
              {errors.email && <span className="error-message">{errors.email}</span>}
            </div>

            <div className="form-group">
              <label className="required">Phone</label>
              <input
                type="tel"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                className={errors.phone ? 'error' : ''}
                placeholder="(555) 123-4567"
              />
              {errors.phone && <span className="error-message">{errors.phone}</span>}
            </div>
          </div>
        </div>

        <div className="form-section">
          <h3>Financial Information</h3>
          
          <div className="form-row">
            <div className="form-group">
              <label className="required">Annual Income</label>
              <input
                type="number"
                name="income"
                value={formData.income}
                onChange={handleChange}
                className={errors.income ? 'error' : ''}
                placeholder="0.00"
                min="0"
                step="0.01"
              />
              {errors.income && <span className="error-message">{errors.income}</span>}
            </div>

            <div className="form-group">
              <label className="required">Income Type</label>
              <select
                name="incomeType"
                value={formData.incomeType}
                onChange={handleChange}
                className={errors.incomeType ? 'error' : ''}
              >
                <option value="">Select income type</option>
                <option value="Salary">Salary</option>
                <option value="Self-Employed">Self-Employed</option>
                <option value="Rental Income">Rental Income</option>
              </select>
              {errors.incomeType && <span className="error-message">{errors.incomeType}</span>}
            </div>
          </div>

          <div className="form-group">
            <label className="required">Requested Loan Amount</label>
            <input
              type="number"
              name="requestedLoanAmount"
              value={formData.requestedLoanAmount}
              onChange={handleChange}
              className={errors.requestedLoanAmount ? 'error' : ''}
              placeholder="0.00"
              min="0"
              step="0.01"
            />
            {errors.requestedLoanAmount && <span className="error-message">{errors.requestedLoanAmount}</span>}
          </div>
        </div>

        {formData.addressId && (
          <input type="hidden" name="addressId" value={formData.addressId} />
        )}

        <div className="form-actions">
          <Link to="/loan-applications" className="btn-secondary">
            Cancel
          </Link>
          <button 
            type="submit" 
            className="btn-primary"
            disabled={loading}
          >
            {loading ? 'Submitting...' : (isEditing ? 'Update Application' : 'Submit Application')}
          </button>
        </div>
      </form>
    </div>
  );
}

export default LoanApplicationForm;