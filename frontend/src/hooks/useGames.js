import { useCallback, useEffect, useState } from "react";
import { getGames } from "../api/api";

export function useGames() {
  const [games, setGames] = useState([]);
  const [page, setPage] = useState(0);
  const [initialLoading, setInitialLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [error, setError] = useState("");
  const [hasMore, setHasMore] = useState(true);
  const [filterTags, setFilterTags] = useState([]);

  const loadPage = useCallback(async (pageToLoad, tags) => {
    try {
      setError("");

      if (pageToLoad === 0) {
        setInitialLoading(true);
      } else {
        setLoadingMore(true);
      }

      const data = await getGames(pageToLoad, tags);
      const nextGames = data.content || [];

      setGames((prevGames) => {
        if (pageToLoad === 0) return nextGames;

        const existingIds = new Set(prevGames.map((game) => game.id));
        return [
          ...prevGames,
          ...nextGames.filter((game) => !existingIds.has(game.id)),
        ];
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
    loadPage(0, filterTags);
  }, [filterTags, loadPage]);

  const loadMore = useCallback(() => {
    if (initialLoading || loadingMore || !hasMore) return;
    loadPage(page + 1, filterTags);
  }, [page, initialLoading, loadingMore, hasMore, filterTags, loadPage]);

  return {
    games,
    error,
    initialLoading,
    loadingMore,
    hasMore,
    loadMore,
    setFilterTags,
  };
}
