import React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/auth-context";

function Navbar() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  function handleLogout() {
    logout();
    navigate("/auth");
  }

  return (
    <nav>
      <div>
        <NavLink
          to="/games"
          className={({ isActive }) => (isActive ? "active-link" : "")}
        >
          Игры
        </NavLink>

        {!user ? (
          <NavLink
            to="/auth"
            className={({ isActive }) => (isActive ? "active-link" : "")}
          >
            Войти
          </NavLink>
        ) : null}
      </div>

      {user ? (
        <button type="button" onClick={handleLogout}>
          Выйти
        </button>
      ) : null}
    </nav>
  );
}

export default Navbar;
