import React from "react";
import { Navigate, Outlet } from "react-router-dom";

const ProtectedRoute = ({ isAuthenticated, redirectPath = "/auth/login", children }) => {
    if (!isAuthenticated) {
        return <Navigate to={redirectPath} replace />;
    }
    return children || <Outlet />;
};

export default ProtectedRoute;
