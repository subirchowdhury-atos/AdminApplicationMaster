import { Outlet } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import Header from '../components/layout/Header';
import Sidebar from '../components/layout/Sidebar';

/**
 * Main Layout Component
 * Wraps all authenticated pages with header, sidebar
 */
function MainLayout() {
  const { user } = useAuth();

  return (
    <div className={user ? 'no-skin' : 'login-layout light-login'} 
         style={{ fontFamily: "'Open Sans','Helvetica Neue',Helvetica,Arial,sans-serif" }}>
      <Header />
      
      <div className="main-container" id="main-container">
        {user && <Sidebar />}
        
        <div className="main-content">
          <Outlet /> {/* Child routes render here */}
        </div>
      </div>
    </div>
  );
}

export default MainLayout;