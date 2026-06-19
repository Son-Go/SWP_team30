import React, { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { deleteGame, getGameById, updateGame } from "../api/api";
import ErrorState from "../components/ErrorState";
import Loader from "../components/Loader";

function GamePage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [game, setGame] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [banner, setBanner] = useState(null);

  useEffect(() => {
    async function loadGame() {
      try {
        setLoading(true);
        setError("");

        const data = await getGameById(id);
        setGame(data);
        setTitle(data.title || "");
        setDescription(data.description || "");
      } catch (err) {
        setError(err.message || "Не удалось загрузить игру");
      } finally {
        setLoading(false);
      }
    }

    loadGame();
  }, [id]);

  async function handleUpdate(event) {
    event.preventDefault();

    try {
      setIsSubmitting(true);
      setError("");

      const formData = new FormData();
      formData.append("title", title);
      formData.append("description", description);

      if (banner) {
        formData.append("banner", banner);
      }

      const updatedGame = await updateGame(id, formData);
      setGame(updatedGame);
      setTitle(updatedGame.title || "");
      setDescription(updatedGame.description || "");
      setBanner(null);
      setIsEditing(false);
    } catch (err) {
      setError(err.message || "Не удалось обновить игру");
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleDelete() {
    const confirmed = window.confirm("Уверен? Дороги назад не будет.");

    if (!confirmed) {
      return;
    }

    try {
      setIsSubmitting(true);
      setError("");

      await deleteGame(id);
      navigate("/games");
    } catch (err) {
      setError(err.message || "Не удалось удалить игру(к сожалению)");
      setIsSubmitting(false);
    }
  }

  if (loading) {
    return <Loader text="Загрузка игры..." />;
  }

  if (error && !game) {
    return <ErrorState message={error} />;
  }

  if (!game) {
    return <ErrorState message="Игра не найдена))" />;
  }

  return (
    <section className="section-lg">
      <div className="page-header">
        <div className="section">
          <Link to="/games" className="nav-link">
            ← Назад к каталогу
          </Link>
          <h1 className="page-title">{game.title}</h1>
        </div>

        <div className="card-actions">
          {!isEditing ? (
            <button
              type="button"
              className="button"
              onClick={() => setIsEditing(true)}
            >
              Редактировать игру
            </button>
          ) : (
            <button
              type="button"
              className="button button-ghost"
              onClick={() => {
                setIsEditing(false);
                setTitle(game.title || "");
                setDescription(game.description || "");
                setBanner(null);
                setError("");
              }}
            >
              Отмена
            </button>
          )}

          <button
            type="button"
            className="button button-danger"
            onClick={handleDelete}
            disabled={isSubmitting}
          >
            Удалить игру к чертям
          </button>
        </div>
      </div>

      {error ? <ErrorState message={error} /> : null}

      {!isEditing ? (
        <article className="card section-lg">
          {game.bannerUrl ? (
            <img src={game.bannerUrl} alt={game.title} />
          ) : (
            <div className="state-box">Баннер пока не загружен.</div>
          )}

          <div className="section">
            <h2 className="card-title">Описание</h2>
            <p className="card-text">{game.description || "Описания нема."}</p>
          </div>
        </article>
      ) : (
        <article className="card">
          <form className="form" onSubmit={handleUpdate}>
            <div className="form-group">
              <label className="label" htmlFor="title">
                Название
              </label>
              <input
                id="title"
                className="input"
                type="text"
                value={title}
                onChange={(event) => setTitle(event.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="label" htmlFor="description">
                Описание
              </label>
              <textarea
                id="description"
                className="textarea"
                value={description}
                onChange={(event) => setDescription(event.target.value)}
              />
            </div>

            <div className="form-group">
              <label className="label" htmlFor="banner">
                Новый баннер
              </label>
              <input
                id="banner"
                className="file-input"
                type="file"
                accept="image/*"
                onChange={(event) => setBanner(event.target.files[0] || null)}
              />
            </div>

            <div className="card-actions">
              <button
                type="submit"
                className="button button-secondary"
                disabled={isSubmitting}
              >
                {isSubmitting ? "Сохранение..." : "Сохранить"}
              </button>

              <button
                type="button"
                className="button button-ghost"
                onClick={() => {
                  setIsEditing(false);
                  setTitle(game.title || "");
                  setDescription(game.description || "");
                  setBanner(null);
                  setError("");
                }}
              >
                Отмена
              </button>
            </div>
          </form>
        </article>
      )}
    </section>
  );
}

export default GamePage;
