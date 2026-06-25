import React, { useEffect, useState } from "react";
import { AuthContext } from "./auth-context";
import {
  clearStoredToken,
  getCurrentUser,
  getStoredToken,
  loginUser,
  setStoredToken,
} from "../api/api";

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => getStoredToken() || "");
  const [user, setUser] = useState(null);
  const [authLoading, setAuthLoading] = useState(true);

  useEffect(() => {
    async function loadUser() {
      if (!token) {
        setUser(null);
        setAuthLoading(false);
        return;
      }

      try {
        const data = await getCurrentUser(token);
        setUser(data.user || data);
      } catch (error) {
        clearStoredToken();
        setToken("");
        setUser(null);
      } finally {
        setAuthLoading(false);
      }
    }

    loadUser();
  }, [token]);

  const login = async (credentials) => {
    const data = await loginUser(credentials);
    const nextToken = data.token || data.sessionToken || data.accessToken;

    // console.log("ответ от сервера при логине:", data);

    if (!nextToken) {
      throw new Error("Токен не пришел с сервера");
    }

    setStoredToken(nextToken);
    setToken(nextToken);

    const currentUser = await getCurrentUser(nextToken);
    const nextUser = currentUser.user || currentUser;
    setUser(nextUser);

    return nextUser;
  };

  const logout = () => {
    clearStoredToken();
    setToken("");
    setUser(null);
  };

  const value = {
    token,
    user,
    isAuthenticated: Boolean(token && user),
    authLoading,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
