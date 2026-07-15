import React from "react";
import { createBrowserRouter, Navigate } from "react-router-dom";
import Layout from "./components/Layout";
import ProtectedRoute from "./components/ProtectedRoute";
import AuthPage from "./pages/AuthPage";
import CreateGamePage from "./pages/CreateGamePage";
import GamePage from "./pages/GamePage";
import GamesPage from "./pages/GamesPage";
import NotFoundPage from "./pages/NotFoundPage";
import AdminRoute from "./components/AdminRoute";
import AdminProfilePage from "./pages/AdminProfilePage";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        index: true,
        element: <Navigate to="/games" replace />,
      },
      {
        path: "games",
        element: <GamesPage />,
      },
      {
        element: <ProtectedRoute />,
        children: [
          {
            path: "games/create",
            element: <CreateGamePage />,
          },
        ],
      },
      {
        element: <AdminRoute />,
        children: [
          {
            path: "admin",
            element: <AdminProfilePage />,
          },
        ],
      },
      {
        path: "games/:id",
        element: <GamePage />,
      },
      {
        path: "*",
        element: <NotFoundPage />,
      },
    ],
  },
  {
    path: "/auth",
    element: <AuthPage />,
  },
]);

export default router;
