import { useCallback, useEffect, useState } from "react";
import { getGames } from "../api/api";

export function useGames() {
  const [games, setGames] = useState([]);
  const [page, setPage] = useState(0);
  const [initialLoading, setInitialLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [error, setError] = useState("");
  const [hasMore, setHasMore] = useState(true);

  const loadPage = useCallback(async (pageToLoad) => {
    try {
      setError("");

      if (pageToLoad === 0) {
        setInitialLoading(true);
      } else {
        setLoadingMore(true);
      }

      const data = await getGames(pageToLoad);
      const nextGames = data.content || [];

      setGames((prevGames) => {
        if (pageToLoad === 0) {
          return nextGames;
        }

        const existingIds = new Set(prevGames.map((game) => game.id));
        const uniqueNextGames = nextGames.filter(
          (game) => !existingIds.has(game.id),
        );

        return [...prevGames, ...uniqueNextGames];
      });

      setPage(data.number);
      setHasMore(!data.last);
    } catch (err) {
      setError(err.message || "Не удалось загрузить игры");
    } finally {
      setInitialLoading(false);
      setLoadingMore(false);
    }
  }, []);

  useEffect(() => {
    loadPage(0);
  }, [loadPage]);

  const loadMore = useCallback(() => {
    if (initialLoading || loadingMore || !hasMore) {
      return;
    }

    loadPage(page + 1);
  }, [page, initialLoading, loadingMore, hasMore, loadPage]);

  return {
    games,
    error,
    initialLoading,
    loadingMore,
    hasMore,
    loadMore,
  };
}
