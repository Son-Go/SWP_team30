const API_URL = import.meta.env.VITE_API_URL;
const USE_MOCK_AUTH = true;

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

export function getGameById(id) {
  return request(`/games/${id}`);
}

export function createGame(formData) {
  return request("/games", {
    method: "POST",
    body: formData,
  });
}

export function updateGame(id, formData) {
  return request(`/games/${id}`, {
    method: "PATCH",
    body: formData,
  });
}

export function deleteGame(id) {
  return request(`/games/${id}`, {
    method: "DELETE",
  });
}

export async function loginUser(credentials) {
  if (USE_MOCK_AUTH) {
    await new Promise((resolve) => setTimeout(resolve, 400));

    return {
      id: 1,
      username:
        credentials.username || credentials.email?.split("@")[0] || "demo_user",
      email: credentials.email || "demo@example.com",
      createdAt: new Date().toISOString(),
    };
  }

  return request("/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(credentials),
  });
}

export async function registerUser(userData) {
  if (USE_MOCK_AUTH) {
    await new Promise((resolve) => setTimeout(resolve, 400));

    return {
      id: 2,
      username: userData.username || "new_user",
      email: userData.email || "new@example.com",
      createdAt: new Date().toISOString(),
    };
  }

  return request("/auth/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userData),
  });
}

export async function getCurrentUser() {
  if (USE_MOCK_AUTH) {
    await new Promise((resolve) => setTimeout(resolve, 200));
    throw new Error("No active session");
  }

  return request("/auth/me");
}
