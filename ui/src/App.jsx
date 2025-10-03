import { RouterProvider } from 'react-router-dom';
import { router } from './routes';

/**
 * Main App Component
 * Sets up routing for the entire application
 */
function App() {
  return <RouterProvider router={router} />;
}

export default App;