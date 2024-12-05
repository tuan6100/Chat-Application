import { Suspense, lazy, useContext } from "react";
import { Navigate, useRoutes } from "react-router-dom";
import LoadingScreen from "../component/ScreenLoading";
import MainLayout from "../layout/main";
import ProtectedRoute from "./ProtectRouter";
import AuthContext from "../context/AuthContext";

const Loadable = (Component) => (props) => {
  return (
      <Suspense fallback={<LoadingScreen />}>
        <Component {...props} />
      </Suspense>
  );
};

const LoginPage = Loadable(lazy(() => import("../page/auth/Login")));
const RegisterPage = Loadable(lazy(() => import("../page/auth/Register")));
const ResetPasswordPage = Loadable(lazy(() => import("../page/auth/ResetPassword")));

export default function Router() {
  const { isAuthenticated } = useContext(AuthContext);

  return useRoutes([
    {
      path: "/auth",
      element: <MainLayout />,
      children: [
        { path: "login", element: <LoginPage /> },
        { path: "register", element: <RegisterPage /> },
        { path: "reset-password", element: <ResetPasswordPage /> }
      ],
    },
    {
      path: "/app",
      element: (
          <ProtectedRoute isAuthenticated={isAuthenticated}>

          </ProtectedRoute>
      ),
      children: [

      ],
    },
    { path: "*", element: <Navigate to="/app" replace /> },
  ]);
}
