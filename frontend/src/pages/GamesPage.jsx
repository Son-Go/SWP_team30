import React, { useEffect, useMemo, useRef, useState } from "react";
import { Link, useLocation, useSearchParams } from "react-router-dom";
import GameCard from "../components/GameCard";
import Loader from "../components/Loader";
import ErrorState from "../components/ErrorState";
import EmptyState from "../components/EmptyState";
import { useGames } from "../hooks/useGames";
import { getAllTags, getGames } from "../api/api";

const FILTER_CATEGORIES = [
  { key: "genre", label: "Жанр" },
  { key: "stage", label: "Статус разработки" },
  { key: "town", label: "Город" },
];

function GamesRow({
  title,
  games,
  loading,
  error,
  trackRef,
  onScrollLeft,
  onScrollRight,
}) {
  return (
    <section className="section-lg">
      <div className="catalog-row-header">
        <h2 className="page-title catalog-row-title">{title}</h2>
      </div>

      {loading ? (
        <Loader text="Загружаем игры..." />
      ) : error ? (
        <ErrorState message={error} />
      ) : !games.length ? (
        <EmptyState title="Игры не найдены" message="Пока здесь пусто" />
      ) : (
        <div className="catalog-row-content">
          <button
            type="button"
            className="gallery-arrow-thumb catalog-row-arrow"
            onClick={onScrollLeft}
            aria-label={`Прокрутить раздел ${title} влево`}
          >
            <svg
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2.5"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <polyline points="15 18 9 12 15 6" />
            </svg>
          </button>

          <div className="catalog-row-viewport">
            <div className="catalog-row-track" ref={trackRef}>
              {games.map((game) => (
                <div className="catalog-row-item" key={game.id}>
                  <GameCard game={game} />
                </div>
              ))}
            </div>
          </div>

          <button
            type="button"
            className="gallery-arrow-thumb catalog-row-arrow"
            onClick={onScrollRight}
            aria-label={`Прокрутить раздел ${title} вправо`}
          >
            <svg
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2.5"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <polyline points="9 18 15 12 9 6" />
            </svg>
          </button>
        </div>
      )}
    </section>
  );
}

function GamesPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const location = useLocation();
  const [filterOpen, setFilterOpen] = useState(false);
  const [allTags, setAllTags] = useState({
    genre: [],
    town: [],
    stage: [],
    featured: [],
  });

  const activeTags = useMemo(() => searchParams.getAll("tags"), [searchParams]);
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

  const [newGames, setNewGames] = useState([]);
  const [featuredGames, setFeaturedGames] = useState([]);
  const [newGamesLoading, setNewGamesLoading] = useState(true);
  const [featuredLoading, setFeaturedLoading] = useState(true);
  const [newGamesError, setNewGamesError] = useState("");
  const [featuredError, setFeaturedError] = useState("");

  const sentinelRef = useRef(null);
  const newTrackRef = useRef(null);
  const featuredTrackRef = useRef(null);
  const allGamesSectionRef = useRef(null);

  useEffect(() => {
    getAllTags()
      .then((data) =>
        setAllTags({
          genre: [],
          town: [],
          stage: [],
          featured: [],
          ...(data.gameTags || {}),
        }),
      )
      .catch(() => {});
  }, []);

  useEffect(() => {
    setPendingTags(activeTags);
    setFilterTags(activeTags);
  }, [activeTags, setFilterTags]);

  useEffect(() => {
    if (
      initialLoading ||
      !location.state?.scrollToAllGames ||
      !allGamesSectionRef.current
    ) {
      return;
    }

    const timeoutId = setTimeout(() => {
      const extraOffset = 350;
      const sectionTop =
        allGamesSectionRef.current.getBoundingClientRect().top + window.scrollY;

      window.scrollTo({
        top: sectionTop + extraOffset,
        behavior: "smooth",
      });
    }, 0);

    return () => clearTimeout(timeoutId);
  }, [initialLoading, location.state]);

  useEffect(() => {
    async function loadRows() {
      try {
        setNewGamesLoading(true);
        setNewGamesError("");
        const data = await getGames(0, []);
        setNewGames(data.content || []);
      } catch (err) {
        setNewGamesError(err.message || "Не удалось загрузить новые игры");
      } finally {
        setNewGamesLoading(false);
      }

      try {
        setFeaturedLoading(true);
        setFeaturedError("");
        const data = await getGames(0, ["Featured"]);
        setFeaturedGames(data.content || []);
      } catch (err) {
        setFeaturedError(err.message || "Не удалось загрузить избранные игры");
      } finally {
        setFeaturedLoading(false);
      }
    }

    loadRows();
  }, []);

  useEffect(() => {
    const target = sentinelRef.current;
    if (!target) return;

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          loadMore();
        }
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
      pendingTags.length > 0 ? pendingTags.map((tag) => ["tags", tag]) : {},
    );
    setFilterOpen(false);
  }

  function handleReset() {
    setPendingTags([]);
    setSearchParams({});
    setFilterOpen(false);
  }

  function scrollRow(ref, direction) {
    if (!ref.current) return;

    const card = ref.current.querySelector(".catalog-row-item");
    if (!card) return;

    const styles = window.getComputedStyle(ref.current);
    const gap = parseFloat(styles.columnGap || styles.gap || "0");
    const step = card.offsetWidth + gap;

    ref.current.scrollBy({
      left: direction === "right" ? step : -step,
      behavior: "smooth",
    });
  }

  if (initialLoading && games.length === 0) {
    return <Loader text="Загружаем каталог..." />;
  }

  if (error && games.length === 0) {
    return <ErrorState message={error} />;
  }

  return (
    <section className="section-lg">
      <div className="page-header" style={{ justifyContent: "flex-end" }}>
        <Link to="/games/create" className="button button-secondary">
          Выложить игру
        </Link>
      </div>

      <GamesRow
        title="Новые"
        games={newGames}
        loading={newGamesLoading}
        error={newGamesError}
        trackRef={newTrackRef}
        onScrollLeft={() => scrollRow(newTrackRef, "left")}
        onScrollRight={() => scrollRow(newTrackRef, "right")}
      />

      <GamesRow
        title="Избранное"
        games={featuredGames}
        loading={featuredLoading}
        error={featuredError}
        trackRef={featuredTrackRef}
        onScrollLeft={() => scrollRow(featuredTrackRef, "left")}
        onScrollRight={() => scrollRow(featuredTrackRef, "right")}
      />

      <section className="section-lg" ref={allGamesSectionRef}>
        <div className="page-header">
          <div>
            <div className="page-title">Все игры</div>
          </div>
          <div style={{ display: "flex", gap: "12px", alignItems: "center" }}>
            <button
              className={`button button-ghost filter-toggle ${filterOpen ? "active" : ""}`}
              onClick={() => {
                setPendingTags(activeTags);
                setFilterOpen((value) => !value);
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
          </div>
        </div>

        {filterOpen && (
          <div className="filter-panel">
            {FILTER_CATEGORIES.map(({ key, label }) =>
              (allTags[key] || []).length > 0 ? (
                <div className="filter-category" key={key}>
                  <div className="filter-category-label">{label}</div>
                  <div className="tag-list">
                    {(allTags[key] || []).map((tag) => (
                      <span
                        key={tag}
                        className={`tag-badge tag-badge-selectable ${
                          key === "genre"
                            ? "tag-badge-genre"
                            : key === "stage"
                              ? "tag-badge-stage"
                              : "tag-badge-town"
                        } ${pendingTags.includes(tag) ? "tag-badge-active" : ""}`}
                        onClick={() => togglePending(tag)}
                      >
                        {tag}
                      </span>
                    ))}
                  </div>
                </div>
              ) : null,
            )}

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
    </section>
  );
}

export default GamesPage;
