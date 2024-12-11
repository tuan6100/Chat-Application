import { Suspense, lazy, useContext } from 'react';
import { Navigate, useRoutes } from 'react-router';
import ScreenLoading from '../component/ScreenLoading';
import MainLayout from '../layout/main';
import ProtectedRoute from './ProtectRouter';
import AuthContext from '../context/AuthContext';
import DashboardLayout from "../layout/dashboard";


const Loadable = (Component) => (props) => {
  return (
      <Suspense fallback={<ScreenLoading justifyContent="center" />}>
        <Component {...props} />
      </Suspense>
  );
};

const LoginPage = Loadable(lazy(() => import('../page/auth/Login')));
const RegisterPage = Loadable(lazy(() => import('../page/auth/Register')));
const ValidateEmail = Loadable(lazy(() => import('../page/auth/ValidateEmail')));
const ValidateUsername = Loadable(lazy(() => import('../page/auth/ValidateUsername')));
const RenewPassword = Loadable(lazy(() => import('../page/auth/RenewPassword')));
const SetupProfile = Loadable(lazy(() => import('../page/auth/SetupProfile')));

export default function Router() {
  const { isAuthenticated } = useContext(AuthContext);

  return useRoutes([
    {
      path: '/auth',
      element: <MainLayout />,
      children: [
        { path: 'login', element:  <LoginPage /> },
        { path: 'register', element: <RegisterPage /> },
        { path: 'validate-email', element: <ValidateEmail /> },
        { path: 'validate-username', element: <ValidateUsername /> },
        { path: 'renew-password', element: <RenewPassword /> },
        { path: 'setup-profile', element:<ProtectedRoute isAuthenticated={isAuthenticated}> <SetupProfile /> </ProtectedRoute> }
      ],
    },
    {
      path: '/app',
      element: (
          <ProtectedRoute isAuthenticated={isAuthenticated}>
              <DashboardLayout />
          </ProtectedRoute>
      ),
      children: [

      ],
    },
    { path: '*', element: <Navigate to='/app' replace /> },
  ]);

}
