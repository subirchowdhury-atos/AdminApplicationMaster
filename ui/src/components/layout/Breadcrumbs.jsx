import { Link, useLocation } from 'react-router-dom';

/**
 * Breadcrumbs Component
 * Replaces Rails _breadcrumbs.html.erb
 */
function Breadcrumbs() {
  const location = useLocation();
  
  // Generate breadcrumb items based on current path
  const generateBreadcrumbs = () => {
    const paths = location.pathname.split('/').filter(x => x);
    
    const breadcrumbs = [{ name: 'Home', path: '/' }];
    
    let currentPath = '';
    paths.forEach((path, index) => {
      currentPath += `/${path}`;
      const name = path
        .split('-')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
      
      breadcrumbs.push({
        name: name,
        path: currentPath,
        isLast: index === paths.length - 1
      });
    });
    
    return breadcrumbs;
  };

  const breadcrumbs = generateBreadcrumbs();

  return (
    <div className="breadcrumbs" id="breadcrumbs">
      <ul className="breadcrumb">
        <li>
          <i className="ace-icon fa fa-home home-icon"></i>
          <Link to="/">Home</Link>
        </li>
        {breadcrumbs.slice(1).map((crumb, index) => (
          <li key={index} className={crumb.isLast ? 'active' : ''}>
            {crumb.isLast ? (
              crumb.name
            ) : (
              <Link to={crumb.path}>{crumb.name}</Link>
            )}
          </li>
        ))}
      </ul>

      <div className="nav-search" id="nav-search">
        <form className="form-search" onSubmit={(e) => e.preventDefault()}>
          <span className="input-icon">
            <input 
              type="text" 
              placeholder="Search ..." 
              className="nav-search-input" 
              id="nav-search-input" 
              autoComplete="off" 
            />
            <i className="ace-icon fa fa-search nav-search-icon"></i>
          </span>
        </form>
      </div>
    </div>
  );
}

export default Breadcrumbs;