import { afterEach, beforeEach, describe, expect, it, vi } from "vitest"
import {
  deleteGame,
  getCurrentUser,
  getGameById,
  getGames,
  getStoredToken,
  loginUser,
  registerUser,
  setStoredToken,
  clearStoredToken,
} from "../api/api"

const API_BASE = import.meta.env.VITE_API_URL

describe("api client", () => {
  beforeEach(() => {
    vi.stubGlobal("fetch", vi.fn())
    localStorage.clear()
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it("stores and clears the session token", () => {
    setStoredToken("abc123")
    expect(getStoredToken()).toBe("abc123")

    clearStoredToken()
    expect(getStoredToken()).toBeNull()
  })

  it("requests paginated games from the configured API", async () => {
    fetch.mockResolvedValue({
      ok: true,
      status: 200,
      headers: new Headers({ "content-type": "application/json" }),
      json: async () => ({ content: [], number: 0, last: true }),
    })

    await expect(getGames(2)).resolves.toEqual({
      content: [],
      number: 0,
      last: true,
    })

    expect(fetch).toHaveBeenCalledWith(
      `${API_BASE}/games?page=2`,
      {},
    )
  })

  it("adds the bearer token for authenticated game requests", async () => {
    fetch.mockResolvedValue({
      ok: true,
      status: 200,
      headers: new Headers({ "content-type": "application/json" }),
      json: async () => ({ id: 7 }),
    })

    await getGameById(7, "token-123")

    expect(fetch).toHaveBeenCalledWith(
      `${API_BASE}/games/7`,
      {
        headers: {
          Authorization: "Bearer token-123",
        },
      },
    )
  })

  it("sends login credentials as JSON", async () => {
    fetch.mockResolvedValue({
      ok: true,
      status: 200,
      headers: new Headers({ "content-type": "application/json" }),
      json: async () => ({ token: "jwt-token" }),
    })

    await expect(
      loginUser({ authInfo: "user@example.com", password: "secret" }),
    ).resolves.toEqual({ token: "jwt-token" })

    expect(fetch).toHaveBeenCalledWith(
      `${API_BASE}/auth/login`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          authInfo: "user@example.com",
          isEmail: true,
          password: "secret",
        }),
      },
    )
  })

  it("surfaces backend error messages for failed requests", async () => {
    fetch.mockResolvedValue({
      ok: false,
      status: 401,
      headers: new Headers({ "content-type": "application/json" }),
      json: async () => ({ message: "Incorrect password" }),
    })

    await expect(
      loginUser({ authInfo: "user@example.com", password: "wrong" }),
    ).rejects.toThrow("Incorrect password")
  })

  it("supports registration and authenticated user lookup", async () => {
    fetch
      .mockResolvedValueOnce({
        ok: true,
        status: 201,
        headers: new Headers({ "content-type": "application/json" }),
        json: async () => ({ token: "new-user-token" }),
      })
      .mockResolvedValueOnce({
        ok: true,
        status: 200,
        headers: new Headers({ "content-type": "application/json" }),
        json: async () => ({
          id: 3,
          username: "new-user",
          email: "new@example.com",
          profileImageUrl: null,
        }),
      })

    await expect(
      registerUser({
        username: "new-user",
        email: "new@example.com",
        password: "secret",
      }),
    ).resolves.toEqual({ token: "new-user-token" })

    await expect(getCurrentUser("new-user-token")).resolves.toEqual({
      id: 3,
      username: "new-user",
      email: "new@example.com",
      profileImageUrl: null,
    })
  })

  it("sends delete requests with the bearer token", async () => {
    fetch.mockResolvedValue({
      ok: true,
      status: 204,
      headers: new Headers(),
      text: async () => "",
    })

    await expect(deleteGame(10, "token-456")).resolves.toBeNull()

    expect(fetch).toHaveBeenCalledWith(
      `${API_BASE}/games/10`,
      {
        method: "DELETE",
        headers: {
          Authorization: "Bearer token-456",
        },
      },
    )
  })
})
