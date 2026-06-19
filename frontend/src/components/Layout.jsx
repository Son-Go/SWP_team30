import React from "react";
import { Outlet } from "react-router-dom";
import Navbar from "./NavBar";

function Layout() {
  return (
    <div className="page-shell">
      <div className="navbar-wrap">
        <Navbar />
      </div>

      <main className="page">
        <Outlet />
      </main>
    </div>
  );
}

export default Layout;
