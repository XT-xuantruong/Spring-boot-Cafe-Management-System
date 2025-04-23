import { Navigate, Route, Routes } from 'react-router-dom';
import { privateRoutes, publicRoutes, } from '@/routes';
import './App.css';
import NotFoundPage from './pages/NotFoundPage';
import { useSelector } from 'react-redux';
import { RootState } from './stores';

// Component để bảo vệ private routes
const ProtectedRoute = ({ children, isAdminRoute = false }: { children: React.ReactNode; isAdminRoute?: boolean }) => {
  const accessToken = useSelector((state: RootState) => state.auth.token?.accessToken);
  const userRole = useSelector((state: RootState) => state.auth.user?.role);
  const isAuthenticated = !!accessToken;

  // Nếu yêu cầu là admin route, kiểm tra vai trò
  if (isAdminRoute) {
    const isAdminOrStaff = userRole === 'ADMIN' || userRole === 'STAFF';
    return isAuthenticated && isAdminOrStaff ? (
      children
    ) : (
      <Navigate to="/" replace />
    );
  }

  // Nếu không phải admin route, chỉ kiểm tra xác thực
  return isAuthenticated ? children : <Navigate to="/login" replace />;
};

export default function App() {
  return (
    <>
      <Routes>
        {/* Public Routes */}
        {publicRoutes.map(({ path, component: Component, layout: Layout }) => (
          <Route
            key={path}
            path={path}
            element={
              Layout ? (
                <Layout>
                  <Component />
                </Layout>
              ) : (
                <Component />
              )
            }
          />
        ))}

        {/* Private Routes */}
        {privateRoutes.map(({ path, component: Component, layout: Layout }) => (
          <Route
            key={path}
            path={path}
            element={
              <ProtectedRoute isAdminRoute={path.startsWith('/admin')}>
                {Layout ? (
                  <Layout>
                    <Component />
                  </Layout>
                ) : (
                  <Component />
                )}
              </ProtectedRoute>
            }
          />
        ))}

        {/* Route 404 (tùy chọn) */}
        <Route path="*" element={<NotFoundPage/>} />
      </Routes>
    </>
  );
}