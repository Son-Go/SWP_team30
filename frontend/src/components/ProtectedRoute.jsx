import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../context/auth-context";

function ProtectedRoute() {
  const { isAuthenticated, authLoading } = useAuth();

  if (authLoading) {
    return <div className="page">Загрузка...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/auth" replace />;
  }

  return <Outlet />;
}

export default ProtectedRoute;
