import React, { useEffect, useRef, useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import GameCard from "../components/GameCard";
import Loader from "../components/Loader";
import ErrorState from "../components/ErrorState";
import EmptyState from "../components/EmptyState";
import { useGames } from "../hooks/useGames";
import { getAllTags } from "../api/api";

function GamesPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [filterOpen, setFilterOpen] = useState(false);
  const [allTags, setAllTags] = useState([]);

  // Читаем теги прямо из URL (?tags=RPG&tags=Action)
  const activeTags = searchParams.getAll("tags");
  const [pendingTags, setPendingTags] = useState(activeTags);

  const {
    games,
    error,
    initialLoading,
    loadingMore,
    hasMore,
    loadMore,
    setFilterTags,
  } = useGames();

  const sentinelRef = useRef(null);

  useEffect(() => {
    getAllTags()
      .then((data) => setAllTags(data.gameTags || []))
      .catch(() => {});
  }, []);

  useEffect(() => {
    setFilterTags(activeTags);
  }, [searchParams]);

  useEffect(() => {
    const target = sentinelRef.current;
    if (!target) return;
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) loadMore();
      },
      { root: null, rootMargin: "200px", threshold: 0 },
    );
    observer.observe(target);
    return () => observer.disconnect();
  }, [loadMore]);

  function togglePending(tag) {
    setPendingTags((prev) =>
      prev.includes(tag) ? prev.filter((t) => t !== tag) : [...prev, tag],
    );
  }

  function handleApply() {
    setSearchParams(
      pendingTags.length > 0 ? pendingTags.map((t) => ["tags", t]) : {},
    );
    setFilterOpen(false);
  }

  function handleReset() {
    setPendingTags([]);
    setSearchParams({});
    setFilterOpen(false);
  }

  if (initialLoading) return <Loader text="Загружаем игры..." />;
  if (error && games.length === 0) return <ErrorState message={error} />;

  return (
    <section className="section-lg">
      <div className="page-header">
        <div>
          <div className="page-title">Последние загруженные игры</div>
        </div>
        <div style={{ display: "flex", gap: "12px", alignItems: "center" }}>
          <button
            className={`button button-ghost filter-toggle ${filterOpen ? "active" : ""}`}
            onClick={() => {
              setPendingTags(activeTags);
              setFilterOpen((v) => !v);
            }}
          >
            <svg
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <line x1="4" y1="6" x2="20" y2="6" />
              <line x1="8" y1="12" x2="16" y2="12" />
              <line x1="11" y1="18" x2="13" y2="18" />
            </svg>
            Фильтры
            {activeTags.length > 0 && (
              <span className="filter-badge">{activeTags.length}</span>
            )}
          </button>
          <Link to="/games/create" className="button button-secondary">
            Выложить игру
          </Link>
        </div>
      </div>

      {filterOpen && (
        <div className="filter-panel">
          <div className="tag-list">
            {allTags.map((tag) => (
              <span
                key={tag}
                className={`tag-badge tag-badge-selectable ${pendingTags.includes(tag) ? "tag-badge-filter-active" : ""}`}
                onClick={() => togglePending(tag)}
              >
                {tag}
              </span>
            ))}
          </div>
          <div className="filter-panel-actions">
            <button className="button button-secondary" onClick={handleApply}>
              Применить
            </button>
            {activeTags.length > 0 && (
              <button className="button button-ghost" onClick={handleReset}>
                Сбросить
              </button>
            )}
          </div>
        </div>
      )}

      {!games.length ? (
        <EmptyState
          title="Игры не найдены"
          message="Попробуй изменить фильтры"
        />
      ) : (
        <div className="games-grid">
          {games.map((game) => (
            <GameCard key={game.id} game={game} />
          ))}
        </div>
      )}

      {error ? <ErrorState message={error} /> : null}
      {loadingMore ? <Loader text="Подгружаем еще игры..." /> : null}
      {hasMore ? <div ref={sentinelRef} style={{ height: "24px" }} /> : null}
    </section>
  );
}

export default GamesPage;
