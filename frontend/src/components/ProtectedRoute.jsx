import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';

/**
 * Wraps routes that require authentication.
 *
 * If the user is not authenticated, they are redirected to /login
 * with the original path stored in location state so we can redirect
 * back after a successful login.
 *
 * Optionally accepts a `roles` prop to restrict access by role.
 *
 * @param {Object} props
 * @param {React.ReactNode} props.children - the protected component
 * @param {string[]} [props.roles] - allowed roles (e.g. ['ADMIN', 'DONOR'])
 */
function ProtectedRoute({ children, roles }) {
  const { user, isAuthenticated } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (roles && roles.length > 0 && !roles.includes(user?.role)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
}

export default ProtectedRoute;
