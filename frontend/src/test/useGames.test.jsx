import { act, renderHook, waitFor } from "@testing-library/react"
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest"
import { useGames } from "../hooks/useGames"

vi.mock("../api/api", () => ({
  getGames: vi.fn(),
}))

import { getGames } from "../api/api"

describe("useGames", () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it("loads the first page on mount", async () => {
    getGames.mockResolvedValue({
      content: [
        { id: 1, title: "First game" },
        { id: 2, title: "Second game" },
      ],
      number: 0,
      last: false,
    })

    const { result } = renderHook(() => useGames())

    await waitFor(() => {
      expect(result.current.initialLoading).toBe(false)
    })

    expect(result.current.games).toEqual([
      { id: 1, title: "First game" },
      { id: 2, title: "Second game" },
    ])
    expect(result.current.hasMore).toBe(true)
    expect(getGames).toHaveBeenCalledWith(0)
  })

  it("appends only unique games when loading more pages", async () => {
    getGames
      .mockResolvedValueOnce({
        content: [
          { id: 1, title: "First game" },
          { id: 2, title: "Second game" },
        ],
        number: 0,
        last: false,
      })
      .mockResolvedValueOnce({
        content: [
          { id: 2, title: "Second game" },
          { id: 3, title: "Third game" },
        ],
        number: 1,
        last: true,
      })

    const { result } = renderHook(() => useGames())

    await waitFor(() => {
      expect(result.current.initialLoading).toBe(false)
    })

    await act(async () => {
      result.current.loadMore()
    })

    await waitFor(() => {
      expect(result.current.loadingMore).toBe(false)
    })

    expect(result.current.games).toEqual([
      { id: 1, title: "First game" },
      { id: 2, title: "Second game" },
      { id: 3, title: "Third game" },
    ])
    expect(result.current.hasMore).toBe(false)
    expect(getGames).toHaveBeenNthCalledWith(2, 1)
  })

  it("exposes request errors to the UI", async () => {
    getGames.mockRejectedValue(new Error("Backend unavailable"))

    const { result } = renderHook(() => useGames())

    await waitFor(() => {
      expect(result.current.initialLoading).toBe(false)
    })

    expect(result.current.error).toBe("Backend unavailable")
    expect(result.current.games).toEqual([])
  })
})
