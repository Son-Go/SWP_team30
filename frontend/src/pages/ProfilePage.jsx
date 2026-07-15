import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import ErrorState from "../components/ErrorState";
import GameCard from "../components/GameCard";
import { useAuth } from "../context/auth-context";
import {
  changeUserPassword,
  deleteUserAccount,
  getUserGamesList,
  updateUserProfile,
} from "../api/api";

function ProfilePage() {
  const navigate = useNavigate();
  const { token, user, authLoading, logout, updateUser } = useAuth();

  const [profile, setProfile] = useState({
    username: user?.username ?? "",
    email: user?.email ?? "",
    profileImageUrl: user?.profileImageUrl ?? "",
  });

  const [profileLoading, setProfileLoading] = useState(false);
  const [profileError, setProfileError] = useState("");
  const [profileSuccess, setProfileSuccess] = useState("");

  const [passwordLoading, setPasswordLoading] = useState(false);
  const [passwordError, setPasswordError] = useState("");
  const [passwordSuccess, setPasswordSuccess] = useState("");
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");

  const [deleteLoading, setDeleteLoading] = useState(false);
  const [deleteError, setDeleteError] = useState("");
  const [confirmDelete, setConfirmDelete] = useState(false);

  const [games, setGames] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [gamesLoading, setGamesLoading] = useState(false);
  const [gamesError, setGamesError] = useState("");

  useEffect(() => {
    if (authLoading || !user?.id) return;
    async function loadGames() {
      try {
        setGamesLoading(true);
        setGamesError("");
        const data = await getUserGamesList(user.id, page);
        setGames(data?.content || []);
        setTotalPages(data?.totalPages ?? 0);
      } catch (err) {
        setGamesError(err.message || "Не удалось загрузить игры");
      } finally {
        setGamesLoading(false);
      }
    }
    loadGames();
  }, [authLoading, user?.id, page]);

  async function handleProfileSubmit(event) {
    event.preventDefault();
    try {
      setProfileLoading(true);
      setProfileError("");
      setProfileSuccess("");

      const body = {};
      const trimmedUsername = profile.username.trim();
      const trimmedEmail = profile.email.trim();
      const trimmedUrl = profile.profileImageUrl.trim();

      if (trimmedUsername) body.username = trimmedUsername;
      if (trimmedEmail) body.email = trimmedEmail;
      if (trimmedUrl) body.profileImageUrl = trimmedUrl;

      const updated = await updateUserProfile(body, token);

      const nextUsername = updated?.username ?? trimmedUsername;
      const nextEmail = updated?.email ?? trimmedEmail;
      const nextImageUrl = updated?.profileImageUrl ?? trimmedUrl;

      setProfile({ username: nextUsername, email: nextEmail, profileImageUrl: nextImageUrl });
      updateUser({ username: nextUsername, email: nextEmail, profileImageUrl: nextImageUrl });
      setProfileSuccess("Профиль успешно обновлён.");
    } catch (err) {
      setProfileError(err.message || "Не удалось обновить профиль");
    } finally {
      setProfileLoading(false);
    }
  }

  async function handlePasswordSubmit(event) {
    event.preventDefault();
    try {
      setPasswordLoading(true);
      setPasswordError("");
      setPasswordSuccess("");
      await changeUserPassword({ oldPassword, newPassword }, token);
      setOldPassword("");
      setNewPassword("");
      setPasswordSuccess("Пароль успешно изменён.");
    } catch (err) {
      setPasswordError(err.message || "Не удалось изменить пароль");
    } finally {
      setPasswordLoading(false);
    }
  }

  async function handleDeleteAccount() {
    try {
      setDeleteLoading(true);
      setDeleteError("");
      await deleteUserAccount(token);
      logout();
      navigate("/auth", { replace: true });
    } catch (err) {
      setDeleteError(err.message || "Не удалось удалить аккаунт");
      setConfirmDelete(false);
    } finally {
      setDeleteLoading(false);
    }
  }

  function renderGames() {
    if (authLoading || gamesLoading) {
      return <p className="page-subtitle">Загружаем игры...</p>;
    }
    if (gamesError) {
      return <ErrorState message={gamesError} />;
    }
    if (games.length === 0) {
      return <p className="page-subtitle">У вас пока нет игр</p>;
    }
    return (
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
            Страница {page + 1}{totalPages ? ` из ${totalPages}` : ""}
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
    );
  }

  return (
    <section className="section-lg">
      <Link to="/games" className="nav-link">
        ← К каталогу
      </Link>

      <article className="card">
        <div className="section">
          {profile.profileImageUrl ? (
            <img
              src={profile.profileImageUrl}
              alt={profile.username}
              className="card-author-avatar"
            />
          ) : (
            <p className="page-subtitle">Аватар не задан</p>
          )}
          <h1 className="page-title">{profile.username || "—"}</h1>
          <p className="page-subtitle">Почта: {profile.email || "—"}</p>
          <p className="page-subtitle">
            Дата регистрации:{" "}
            {user?.createdAt
              ? new Date(user.createdAt).toLocaleDateString("ru-RU")
              : "—"}
          </p>
        </div>
      </article>

      <div className="section">
        <h2 className="page-title">Мои игры</h2>
        {renderGames()}
      </div>

      <article className="card create-game-card">
        {profileError ? <ErrorState message={profileError} /> : null}
        {profileSuccess ? (
          <div className="state-box">{profileSuccess}</div>
        ) : null}

        <form className="form" onSubmit={handleProfileSubmit}>
          <div className="section">
            <h2 className="page-title">Редактировать профиль</h2>
            <p className="page-subtitle">
              Заполните только те поля, которые хотите изменить.
            </p>
          </div>

          <div className="form-group">
            <label className="label" htmlFor="profileUsername">
              Имя пользователя
            </label>
            <input
              id="profileUsername"
              className="input"
              type="text"
              value={profile.username}
              onChange={(event) =>
                setProfile((prev) => ({ ...prev, username: event.target.value }))
              }
            />
          </div>

          <div className="form-group">
            <label className="label" htmlFor="profileEmail">
              Почта
            </label>
            <input
              id="profileEmail"
              className="input"
              type="email"
              value={profile.email}
              onChange={(event) =>
                setProfile((prev) => ({ ...prev, email: event.target.value }))
              }
            />
          </div>

          <div className="form-group">
            <label className="label" htmlFor="profileImageUrl">
              Ссылка на аватар
            </label>
            <input
              id="profileImageUrl"
              className="input"
              type="url"
              value={profile.profileImageUrl}
              placeholder="https://..."
              onChange={(event) =>
                setProfile((prev) => ({ ...prev, profileImageUrl: event.target.value }))
              }
            />
          </div>

          <div className="card-actions">
            <button
              type="submit"
              className="button button-secondary"
              disabled={profileLoading}
            >
              {profileLoading ? "Сохранение..." : "Сохранить"}
            </button>
          </div>
        </form>
      </article>

      <article className="card create-game-card">
        {passwordError ? <ErrorState message={passwordError} /> : null}
        {passwordSuccess ? (
          <div className="state-box">{passwordSuccess}</div>
        ) : null}

        <form className="form" onSubmit={handlePasswordSubmit}>
          <div className="section">
            <h2 className="page-title">Изменить пароль</h2>
          </div>

          <div className="form-group">
            <label className="label" htmlFor="oldPassword">
              Текущий пароль
            </label>
            <input
              id="oldPassword"
              className="input"
              type="password"
              value={oldPassword}
              onChange={(event) => setOldPassword(event.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="label" htmlFor="newPassword">
              Новый пароль
            </label>
            <input
              id="newPassword"
              className="input"
              type="password"
              value={newPassword}
              onChange={(event) => setNewPassword(event.target.value)}
              required
            />
          </div>

          <div className="card-actions">
            <button
              type="submit"
              className="button button-secondary"
              disabled={passwordLoading}
            >
              {passwordLoading ? "Изменение..." : "Изменить пароль"}
            </button>
          </div>
        </form>
      </article>

      <article className="card">
        <div className="section">
          {deleteError ? <ErrorState message={deleteError} /> : null}

          <h2 className="page-title">Удалить аккаунт</h2>
          <p className="page-subtitle">
            Это действие нельзя отменить. Все данные будут удалены.
          </p>

          <div className="card-actions">
            {confirmDelete ? (
              <>
                <span className="page-subtitle">Вы уверены?</span>
                <button
                  type="button"
                  className="button button-secondary"
                  onClick={handleDeleteAccount}
                  disabled={deleteLoading}
                >
                  {deleteLoading ? "Удаление..." : "Подтвердить"}
                </button>
                <button
                  type="button"
                  className="button button-ghost"
                  onClick={() => setConfirmDelete(false)}
                  disabled={deleteLoading}
                >
                  Отмена
                </button>
              </>
            ) : (
              <button
                type="button"
                className="button button-danger"
                onClick={() => setConfirmDelete(true)}
              >
                Удалить аккаунт
              </button>
            )}
          </div>
        </div>
      </article>
    </section>
  );
}

export default ProfilePage;
