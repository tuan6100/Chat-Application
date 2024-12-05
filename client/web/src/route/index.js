import { Suspense, lazy, useContext } from 'react';
import { Navigate, useRoutes } from 'react-router';
import LoadingScreen from '../component/ScreenLoading';
import MainLayout from '../layout/main';
import ProtectedRoute from './ProtectRouter';
import AuthContext from '../context/AuthContext';
import RenewPassword from "../page/auth/RenewPassword";

const Loadable = (Component) => (props) => {
  return (
      <Suspense fallback={<LoadingScreen />}>
        <Component {...props} />
      </Suspense>
  );
};

const LoginPage = Loadable(lazy(() => import('../page/auth/Login')));
const RegisterPage = Loadable(lazy(() => import('../page/auth/Register')));
const ValidateEmail = Loadable(lazy(() => import('../page/auth/ValidateEmail')));
const ValidateUsername = Loadable(lazy(() => import('../page/auth/ValidateUsername')));
const NewPasswordPage = Loadable(lazy(() => import('../page/auth/RenewPassword')));

export default function Router() {
  const { isAuthenticated } = useContext(AuthContext);

  return useRoutes([
    {
      path: '/auth',
      element: <MainLayout />,
      children: [
        { path: 'login', element: <LoginPage /> },
        { path: 'register', element: <RegisterPage /> },
        { path: 'validate-email', element: <ValidateEmail /> },
        { path: 'validate-username', element: <ValidateUsername /> },
        { path: 'renew-password', element: <RenewPassword /> }
      ],
    },
    {
      path: '/app',
      element: (
          <ProtectedRoute isAuthenticated={isAuthenticated}>

          </ProtectedRoute>
      ),
      children: [

      ],
    },
    { path: '*', element: <Navigate to='/app' replace /> },
  ]);
}
