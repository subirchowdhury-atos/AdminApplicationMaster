import { useAuth } from '../../hooks/useAuth';
import '../../styles/Header.css';

function Header() {
  const { user, logout } = useAuth();

  const handleMenuToggle = () => {
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.querySelector('.main-content');
    
    sidebar?.classList.toggle('menu-min');
    mainContent?.classList.toggle('expanded');
    
    // Save state to localStorage
    const isCollapsed = sidebar?.classList.contains('menu-min');
    localStorage.setItem('sidebarCollapsed', isCollapsed);
  };

  return (
    <div className="navbar navbar-default" id="navbar">
      <div className="navbar-container" id="navbar-container">
        <button 
          type="button" 
          className="navbar-toggle menu-toggler pull-left" 
          id="menu-toggler"
          data-target="#sidebar"
          onClick={handleMenuToggle}
        >
          <span className="sr-only">Toggle sidebar</span>
          <span className="icon-bar"></span>
          <span className="icon-bar"></span>
          <span className="icon-bar"></span>
        </button>

        <div className="navbar-header pull-left">
          <a href="/" className="navbar-brand">
            <small>
              <i className="fa fa-cogs"></i>
              Admin Portal
            </small>
          </a>
        </div>

        <div className="navbar-buttons navbar-header pull-right" role="navigation">
          <ul className="nav ace-nav">
            {user && (
              <>
                <li className="green">
                  <a href="#">
                    Welcome: {user.firstName || 'Admin'}
                  </a>
                </li>
                <li className="grey">
                  <a href="#" onClick={(e) => { e.preventDefault(); logout(); }}>
                    <small>
                      <i className="ace-icon fa fa-power-off red"></i>
                    </small>
                    Logout
                  </a>
                </li>
              </>
            )}
          </ul>
        </div>
      </div>
    </div>
  );
}

export default Header;