import React from "react";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import Navbar from "./NavBar";
import { useAuth } from "../context/auth-context";

function Layout() {
  const navigate = useNavigate();

  const location = useLocation();
  const isAboutPage = location.pathname === "/";

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

      <main className={isAboutPage ? "about-page-outlet" : "page"}>
        <Outlet />
      </main>
    </div>
  );
}

export default Layout;
