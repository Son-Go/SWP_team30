import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import Loader from "./Loader";
import { useAuth } from "../context/auth-context";

function AdminRoute() {
    const { user, isAuthenticated, authLoading } = useAuth();

    if (authLoading) {
        return <Loader text="Проверяем права доступа..." />;
    }

    if (!isAuthenticated) {
        return <Navigate to="/auth" replace />;
    }

    if (user?.userRole !== "ADMIN") {
        return <Navigate to="/games" replace />;
    }

    return <Outlet />;
}

export default AdminRoute;