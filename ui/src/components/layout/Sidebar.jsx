import { Link, useLocation } from 'react-router-dom';
import '../../styles/Sidebar.css';

/**
 * Sidebar Component
 * Replaces Rails _sidebar.html.erb
 */
function Sidebar() {
  const location = useLocation();

  const isActive = (path) => {
    return location.pathname === path || location.pathname.startsWith(path + '/');
  };

  return (
    <div 
      id="sidebar" 
      className="sidebar responsive" 
      data-sidebar="true" 
      data-sidebar-hover="true" 
      data-sidebar-scroll="true"
    >
      <div className="sidebar-shortcuts" id="sidebar-shortcuts">
      </div>
      
      <ul className="nav nav-list" style={{ top: '0px' }}>
        <li className={isActive('/dashboard') ? 'active' : ''}>
          <Link to="/dashboard">
            <i className="menu-icon fa fa-tachometer"></i>
            <span className="menu-text">Dashboard</span>
          </Link>
          <b className="arrow"></b>
        </li>
        
        <li className={isActive('/loan-applications') ? 'active' : ''}>
          <Link to="/loan-applications">
            <i className="menu-icon fa fa-credit-card"></i>
            <span className="menu-text">Loan Applications</span>
          </Link>
          <b className="arrow"></b>
        </li>
        
        <li className={isActive('/users') ? 'active' : ''}>
          <Link to="/users">
            <i className="menu-icon fa fa-users"></i>
            <span className="menu-text">Users</span>
          </Link>
          <b className="arrow"></b>
        </li>
      </ul>
      
      <div className="nav-list"></div>
    </div>
  );
}

export default Sidebar;