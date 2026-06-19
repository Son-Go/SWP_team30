const API_URL = import.meta.env.VITE_API_URL;
const USE_MOCK_AUTH = false;
const TOKEN_KEY = "session_token";

export function getStoredToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setStoredToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearStoredToken() {
  localStorage.removeItem(TOKEN_KEY);
}

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, options);

  const contentType = response.headers.get("content-type");
  const isJson = contentType && contentType.includes("application/json");

  let data = null;

  if (response.status !== 204) {
    data = isJson ? await response.json() : await response.text();
  }

  if (!response.ok) {
    const errorMessage =
      typeof data === "object" && data !== null && data.message
        ? data.message
        : `Request failed with status ${response.status}`;

    throw new Error(errorMessage);
  }

  return data;
}

export function getGames(page = 0) {
  return request(`/games?page=${page}`);
}

export function getGameById(id, token) {
  return request(`/games/${id}`, {
    headers: token ? { Authorization: `Bearer ${token}` } : undefined,
  });
}

export function createGame(formData, token) {
  return request("/games", {
    method: "POST",
    headers: token ? { Authorization: `Bearer ${token}` } : undefined,
    body: formData,
  });
}

export function updateGame(id, formData, token) {
  return request(`/games/${id}`, {
    method: "PATCH",
    headers: token ? { Authorization: `Bearer ${token}` } : undefined,
    body: formData,
  });
}

export function deleteGame(id, token) {
  return request(`/games/${id}`, {
    method: "DELETE",
    headers: token ? { Authorization: `Bearer ${token}` } : undefined,
  });
}

export async function loginUser(credentials) {
  if (USE_MOCK_AUTH) {
    await new Promise((resolve) => setTimeout(resolve, 400));
    return { token: "mock-session-token" };
  }

  return request("/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      email: credentials.email,
      password: credentials.password,
    }),
  });
}

export async function registerUser(userData) {
  if (USE_MOCK_AUTH) {
    await new Promise((resolve) => setTimeout(resolve, 400));
    return { message: "registered" };
  }

  return request("/auth/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      username: userData.username,
      email: userData.email,
      password: userData.password,
      profileImageUrl: userData.profileImageUrl ?? null,
    }),
  });
}

export async function getCurrentUser(token) {
  if (USE_MOCK_AUTH) {
    await new Promise((resolve) => setTimeout(resolve, 200));

    if (!token) {
      throw new Error("No active session");
    }

    return {
      id: 1,
      username: "demo_user",
      email: "demo@example.com",
      profileImageUrl: null,
      createdAt: new Date().toISOString(),
    };
  }

  return request("/auth/me", {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
}
