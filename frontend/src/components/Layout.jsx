import React from "react";
import { Outlet, useNavigate } from "react-router-dom";
import Navbar from "./NavBar";
import { useAuth } from "../context/auth-context";

function Layout() {
  const navigate = useNavigate();
  const { user, isAuthenticated, logout } = useAuth();

  function handleLogout() {
    logout();
    navigate("/games", { replace: true });
  }

  return (
    <div className="page-shell">
      <div className="navbar-wrap">
        <Navbar
          user={user}
          isAuthenticated={isAuthenticated}
          onLogout={handleLogout}
        />
      </div>

      <main className="page">
        <Outlet />
      </main>
    </div>
  );
}

export default Layout;
