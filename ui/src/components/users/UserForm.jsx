// ========================================
// UserForm.jsx
// ========================================
import { useState } from 'react';

function UserForm({ initialData = {}, onSubmit }) {
  const [formData, setFormData] = useState({
    firstName: initialData.firstName || '',
    lastName: initialData.lastName || '',
    email: initialData.email || '',
    role: initialData.role || '',
    contact: initialData.contact || ''
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await onSubmit(formData);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="row widget-box">
      <div className="widget-header">
        <h4 className="widget-title">Create User</h4>
      </div>
      <div className="col-xs-12">
        <div className="widget-body">
          <div className="widget-main">
            <form className="form-horizontal" onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">First Name</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleChange}
                    className="col-xs-10 col-sm-5"
                    placeholder="First Name"
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Last Name</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleChange}
                    className="col-xs-10 col-sm-5"
                    placeholder="Last Name"
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Email</label>
                <div className="col-sm-9">
                  <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    className="col-xs-10 col-sm-5"
                    placeholder="Email"
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">User Role</label>
                <div className="col-sm-9">
                  <select
                    name="role"
                    value={formData.role}
                    onChange={handleChange}
                    className="col-xs-10 col-sm-5"
                    required
                  >
                    <option value="">Select a Role</option>
                    <option value="admin">admin</option>
                    <option value="contractor">contractor</option>
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label className="col-sm-3 control-label no-padding-right">Contact Number</label>
                <div className="col-sm-9">
                  <input
                    type="text"
                    name="contact"
                    value={formData.contact}
                    onChange={handleChange}
                    className="col-xs-10 col-sm-5"
                    placeholder="Contact Number"
                    required
                  />
                </div>
              </div>

              <div className="space-4"></div>
              <div className="clearfix form-actions">
                <div className="col-md-offset-3 col-md-9">
                  <button type="submit" className="btn btn-info" disabled={loading}>
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

export default UserForm;