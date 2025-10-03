import { createBrowserRouter } from 'react-router-dom';
import MainLayout from '../layouts/MainLayout';
import ProtectedRoute from '../components/auth/ProtectedRoute';

// Auth Pages
import LoginPage from '../pages/LoginPage';
import NewUserPage from '../pages/NewUserPage';

// Dashboard
import DashboardPage from '../pages/DashboardPage';

// Loan Applications
import LoanApplicationsPage from '../pages/LoanApplicationsPage';
import { NewLoanApplicationPage } from '../pages/NewLoanApplicationPage';
import { EditLoanApplicationPage } from '../pages/EditLoanApplicationPage';
import { LoanApplicationDetailPage } from '../pages/LoanApplicationDetailPage';

// Users
import UsersPage from '../pages/UsersPage';

// Error Pages
import NotFoundPage from '../pages/NotFoundPage';

export const router = createBrowserRouter([
  // Public routes
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/register',
    element: <NewUserPage />,
  },
  
  // Protected routes
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <MainLayout />
      </ProtectedRoute>
    ),
    children: [
      // Redirect root to dashboard
      {
        index: true,
        element: <DashboardPage />,
      },
      {
        path: 'dashboard',
        element: <DashboardPage />,
      },
      {
        path: 'loan-applications',
        children: [
          { index: true, element: <LoanApplicationsPage /> },
          { path: 'new', element: <NewLoanApplicationPage /> },
          { path: ':id', element: <LoanApplicationDetailPage /> },
          { path: ':id/edit', element: <EditLoanApplicationPage /> },
        ],
      },
      {
        path: 'users',
        children: [
          { index: true, element: <UsersPage /> },
          { path: 'new', element: <NewUserPage /> },
        ],
      },


    ],
  },
  
  // 404 Not Found
  {
    path: '*',
    element: <NotFoundPage />,
  },
]);