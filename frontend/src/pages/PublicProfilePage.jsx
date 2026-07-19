import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import GameCard from "../components/GameCard";
import Loader from "../components/Loader";
import ErrorState from "../components/ErrorState";
import EmptyState from "../components/EmptyState";
import { getPublicUserProfile, getUserGamesList } from "../api/api";

function PublicProfilePage() {
  const { id } = useParams();

  const [profile, setProfile] = useState(null);
  const [profileLoading, setProfileLoading] = useState(true);
  const [profileError, setProfileError] = useState("");

  const [games, setGames] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [gamesLoading, setGamesLoading] = useState(true);
  const [gamesError, setGamesError] = useState("");

  useEffect(() => {
    async function loadProfile() {
      try {
        setProfileLoading(true);
        setProfileError("");
        const data = await getPublicUserProfile(id);
        setProfile(data?.user || data);
      } catch (err) {
        setProfileError(err.message || "Не удалось загрузить профиль");
      } finally {
        setProfileLoading(false);
      }
    }

    loadProfile();
  }, [id]);

  useEffect(() => {
    async function loadGames() {
      try {
        setGamesLoading(true);
        setGamesError("");
        const data = await getUserGamesList(id, page);
        setGames(data?.content || []);
        setTotalPages(data?.totalPages ?? 0);
      } catch (err) {
        setGamesError(err.message || "Не удалось загрузить игры");
      } finally {
        setGamesLoading(false);
      }
    }

    loadGames();
  }, [id, page]);

  if (profileLoading) {
    return (
      <section className="section-lg">
        <Loader text="Загружаем профиль..." />
      </section>
    );
  }

  if (profileError) {
    return (
      <section className="section-lg">
        <ErrorState message={profileError} />
      </section>
    );
  }

  const createdAt = profile?.createdAt
    ? new Date(profile.createdAt).toLocaleDateString("ru-RU")
    : "—";

  return (
    <section className="section-lg">
      <article className="card">
        <div className="section">
          <h1 className="page-title">{profile?.username || "Пользователь"}</h1>

          {profile?.profileImageUrl ? (
            <img
              src={profile.profileImageUrl}
              alt={profile?.username}
              className="card-author-avatar"
            />
          ) : (
            <p className="page-subtitle">Аватар не задан</p>
          )}

          <p className="page-subtitle">Дата регистрации: {createdAt}</p>
        </div>
      </article>

      <div className="section">
        <h2 className="page-title">Игры</h2>

        {gamesLoading ? (
          <Loader text="Загружаем игры..." />
        ) : gamesError ? (
          <ErrorState message={gamesError} />
        ) : !games.length ? (
          <EmptyState message="У пользователя пока нет игр" />
        ) : (
          <>
            <div className="games-grid">
              {games.map((game) => (
                <GameCard key={game.id} game={game} />
              ))}
            </div>

            <div className="card-actions">
              <button
                type="button"
                className="button button-ghost"
                onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
                disabled={page === 0}
              >
                ← Назад
              </button>
              <span className="page-subtitle">
                Страница {page + 1}
                {totalPages ? ` из ${totalPages}` : ""}
              </span>
              <button
                type="button"
                className="button button-ghost"
                onClick={() => setPage((prev) => prev + 1)}
                disabled={totalPages ? page >= totalPages - 1 : true}
              >
                Вперёд →
              </button>
            </div>
          </>
        )}
      </div>
    </section>
  );
}

export default PublicProfilePage;
