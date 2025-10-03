// ========================================
// NewUserPage.jsx
// ========================================
import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { userApi } from '../api/userApi';
import FlashMessage from '../components/common/FlashMessage';
import UserForm from '../components/users/UserForm';

function NewUserPage() {
  const navigate = useNavigate();
  const [flashMessage, setFlashMessage] = useState(null);
  const [flashType, setFlashType] = useState('info');
  const handleSubmit = async (formData) => {
    try {
      await userApi.create(formData);
      setFlashMessage('User created successfully!');
      setFlashType('success');
      navigate('/users');
    } catch (error) {
      setFlashMessage('Failed to create user');
      setFlashType('error');
    }
  };

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
            User
            <small>
              <i className="ace-icon fa fa-angle-double-right"></i>
            </small>
          </h1>
        </div>
        <UserForm onSubmit={handleSubmit} />
      </div>
    </div>
  );
}

export default NewUserPage;