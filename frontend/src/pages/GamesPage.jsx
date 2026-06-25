import React, { useEffect, useRef } from "react";
import { Link } from "react-router-dom";
import GameCard from "../components/GameCard";
import Loader from "../components/Loader";
import ErrorState from "../components/ErrorState";
import EmptyState from "../components/EmptyState";
import { useGames } from "../hooks/useGames";

function GamesPage() {
  const { games, error, initialLoading, loadingMore, hasMore, loadMore } =
    useGames();

  const sentinelRef = useRef(null);

  useEffect(() => {
    const target = sentinelRef.current;

    if (!target) {
      return;
    }

    const observer = new IntersectionObserver(
      (entries) => {
        const entry = entries[0];

        if (entry.isIntersecting) {
          loadMore();
        }
      },
      {
        root: null,
        rootMargin: "200px",
        threshold: 0,
      },
    );

    observer.observe(target);

    return () => observer.disconnect();
  }, [loadMore]);

  if (initialLoading) {
    return <Loader text="Загружаем игры..." />;
  }

  if (error && games.length === 0) {
    return <ErrorState message={error} />;
  }

  if (!games.length) {
    return (
      <section className="section-lg">
        <div className="page-header">
          <div>
            <h1 className="page-title">Каталог</h1>
          </div>

          <Link to="/games/create" className="button">
            Выложить игру
          </Link>
        </div>

        <EmptyState
          title="Игры пока не найдены"
          message="Добавь первую игру в каталог(лучше не надо)"
        />
      </section>
    );
  }

  return (
    <section className="section-lg">
      <div className="page-header">
        <div>
          <h1 className="page-title">Каталог</h1>
        </div>

        <Link to="/games/create" className="button">
          Создать игру
        </Link>
      </div>

      <div className="games-grid">
        {games.map((game) => (
          <GameCard key={game.id} game={game} />
        ))}
      </div>

      {error ? <ErrorState message={error} /> : null}
      {loadingMore ? <Loader text="Подгружаем еще игры..." /> : null}
      {hasMore ? <div ref={sentinelRef} style={{ height: "24px" }} /> : null}
    </section>
  );
}

export default GamesPage;
