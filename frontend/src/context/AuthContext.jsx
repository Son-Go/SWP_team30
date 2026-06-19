import React, { useEffect, useState } from "react";
import { AuthContext } from "./auth-context";
import { getCurrentUser, loginUser } from "../api/api";

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [authLoading, setAuthLoading] = useState(true);

  useEffect(() => {
    async function loadUser() {
      try {
        const data = await getCurrentUser();
        setUser(data.user || data);
      } catch {
        setUser(null);
      } finally {
        setAuthLoading(false);
      }
    }

    loadUser();
  }, []);

  const login = async (credentials) => {
    const data = await loginUser(credentials);
    const nextUser = data.user || data;
    setUser(nextUser);
    return data;
  };

  const logout = () => {
    setUser(null);
  };

  const value = {
    user,
    isAuthenticated: Boolean(user),
    authLoading,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
