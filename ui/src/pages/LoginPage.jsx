import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import FlashMessage from '../components/common/FlashMessage';
import '../styles/LoginPage.css'; 

function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [flashMessage, setFlashMessage] = useState(null);
  const [flashType, setFlashType] = useState('info');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      await login(email, password);
      navigate('/dashboard');
    } catch (error) {
      setFlashMessage('Invalid email or password');
      setFlashType('error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-container">
        {flashMessage && (
          <FlashMessage
            message={flashMessage}
            type={flashType}
            duration={5000}
            onClose={() => setFlashMessage(null)}
          />
        )}
        
        <div className="login-box">
          <div className="login-header">
            <i className="ace-icon fa fa-cogs"></i>
            <h1>Admin System</h1>
          </div>

          <div className="login-body">
            <h4>Please Enter Your Information</h4>

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="email">
                  <i className="ace-icon fa fa-user"></i>
                </label>
                <input
                  id="email"
                  type="email"
                  className="form-control"
                  placeholder="Email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  autoFocus
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="password">
                  <i className="ace-icon fa fa-lock"></i>
                </label>
                <input
                  id="password"
                  type="password"
                  className="form-control"
                  placeholder="Password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>

              <div className="form-actions">
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={loading}
                >
                  {loading ? (
                    <>
                      <i className="fa fa-spinner fa-spin"></i> Logging in...
                    </>
                  ) : (
                    'Log in'
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>

        <div className="login-footer">
          <p>Test Credentials: admin@example.com / password123</p>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;