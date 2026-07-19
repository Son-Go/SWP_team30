import React from "react";
import { createBrowserRouter } from "react-router-dom";
import Layout from "./components/Layout";
import ProtectedRoute from "./components/ProtectedRoute";
import AuthPage from "./pages/AuthPage";
import CreateGamePage from "./pages/CreateGamePage";
import GamePage from "./pages/GamePage";
import GamesPage from "./pages/GamesPage";
import NotFoundPage from "./pages/NotFoundPage";
import ProfilePage from "./pages/ProfilePage";
import PublicProfilePage from "./pages/PublicProfilePage";
import AdminRoute from "./components/AdminRoute";
import AdminProfilePage from "./pages/AdminProfilePage";
import AboutPage from "./pages/AboutPage";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        index: true,
        element: <AboutPage />,
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
          {
            path: "profile",
            element: <ProfilePage />,
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
        path: "users/:id",
        element: <PublicProfilePage />,
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
