import { Suspense, lazy, useContext } from "react";
import { Navigate, useRoutes } from "react-router-dom";
import LoadingScreen from "../component/ScreenLoading";
import MainLayout from "../layout/main";
import ProtectedRoute from "./ProtectRouter"; // Bảo vệ route
import AuthContext from "../context/AuthContext"; // Import context

const Loadable = (Component) => (props) => {
  return (
      <Suspense fallback={<LoadingScreen />}>
        <Component {...props} />
      </Suspense>
  );
};

const LoginPage = Loadable(lazy(() => import("../page/auth/Login")));
const RegisterPage = Loadable(lazy(() => import("../page/auth/Register")));

export default function Router() {
  const { isAuthenticated } = useContext(AuthContext); // Lấy isAuthenticated từ context

  return useRoutes([
    {
      path: "/auth",
      element: <MainLayout />,
      children: [
        { path: "login", element: <LoginPage /> },
        { path: "register", element: <RegisterPage /> },
      ],
    },
    {
      path: "/app",
      element: (
          <ProtectedRoute isAuthenticated={isAuthenticated}>
            {/* DashboardLayout or other protected components */}
          </ProtectedRoute>
      ),
      children: [
        // Đặt các route cho phần ứng dụng đã đăng nhập ở đây
      ],
    },
    { path: "*", element: <Navigate to="/auth/login" replace /> },
  ]);
}
