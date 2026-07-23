import React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/auth-context";

function Navbar() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  function getNavLinkClassName({ isActive }) {
    return isActive ? "nav-link active-link" : "nav-link";
  }

  function handleLogout() {
    logout();
    navigate("/auth");
  }

  return (
    <header className="navbar">
      <div className="navbar-inner">
        <div className="navbar-left">
          <NavLink to="/" className="brand" aria-label="GDE — на главную">
            <img src="/gde_logo.jpg" alt="GDE" className="brand-logo" />
          </NavLink>

          <NavLink to="/" end className={getNavLinkClassName}>
            О нас
          </NavLink>

          <NavLink to="/games" className={getNavLinkClassName}>
            Игры Сообщества
          </NavLink>

          {user?.userRole === "ADMIN" ? (
            <NavLink to="/admin" className={getNavLinkClassName}>
              Админ панель
            </NavLink>
          ) : null}
        </div>

        <div className="navbar-right">
          {user ? (
            <div
              className="navbar-right-actions"
              style={{ display: "flex", alignItems: "center", gap: "12px" }}
            >
              <NavLink to="/profile" className={getNavLinkClassName}>
                {user.profileImageUrl ? (
                  <img
                    src={user.profileImageUrl}
                    alt={user.username}
                    className="navbar-avatar"
                  />
                ) : (
                  user.username
                )}
              </NavLink>
              <button
                type="button"
                className="button button-ghost"
                onClick={handleLogout}
              >
                Выйти
              </button>
            </div>
          ) : (
            <NavLink to="/auth" className={getNavLinkClassName}>
              Войти
            </NavLink>
          )}
        </div>
      </div>
    </header>
  );
}

export default Navbar;
