import { normalizeMedia } from "../utils/media";

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

export function getGames(page = 0, tags = []) {
  const tagParams = tags.map((t) => `tags=${encodeURIComponent(t)}`).join("&");
  const tagsQuery = tagParams ? `&${tagParams}` : "";
  return request(`/games?page=${page}${tagsQuery}`);
}

export async function getGameById(id, token) {
  const data = await request(`/games/${id}`, {
    headers: token ? { Authorization: `Bearer ${token}` } : undefined,
  });

  return {
    ...data,
    screenshots: normalizeMedia(data?.screenshots),
  };
}

function flattenGameTags(gameTags) {
  if (Array.isArray(gameTags)) {
    return gameTags;
  }

  if (!gameTags || typeof gameTags !== "object") {
    return [];
  }

  return Object.values(gameTags).flat().filter(Boolean);
}

function normalizeGameResponse(game) {
  if (!game) return game;

  return {
    ...game,
    screenshots: normalizeMedia(game?.screenshots),
  };
}

export function createGame(body, token) {
  return request("/games", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify({
      ...body,
      gameTags: flattenGameTags(body?.gameTags),
      screenshots: normalizeMedia(body?.screenshots),
    }),
  }).then(normalizeGameResponse);
}

export function updateGame(id, body, token) {
  return request(`/games/${id}`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify({
      ...body,
      gameTags: flattenGameTags(body?.gameTags),
      screenshots: normalizeMedia(body?.screenshots),
    }),
  }).then(normalizeGameResponse);
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

  const isEmail = credentials.authInfo.includes("@"); // автоопределение

  return request("/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      authInfo: credentials.authInfo, // email или username
      isEmail, // true / false
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

export function getGameAuthor(authorId) {
  return request(`/games/author/${authorId}`);
}

export function getAllTags() {
  return request("/games/tags/all");
}

export function getGameComments(gameId) {
  return request(`/games/${gameId}/comments`);
}

export function createGameComment(gameId, text, token) {
  return request(`/games/${gameId}/comments`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ text }),
  });
}
