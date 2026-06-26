import React, { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { deleteGame, getGameById, updateGame, getGameAuthor } from "../api/api";
import ErrorState from "../components/ErrorState";
import Loader from "../components/Loader";
import { useAuth } from "../context/auth-context";
import TagSelector from "../components/TagSelector";

function GamePage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { token } = useAuth();

  const [game, setGame] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [bannerUrl, setBannerUrl] = useState("");
  const [tags, setTags] = useState([]);
  const [tagInput, setTagInput] = useState("");
  const [authorName, setAuthorName] = useState(null);

  useEffect(() => {
    async function loadGame() {
      try {
        setLoading(true);
        setError("");
        const data = await getGameById(id, token);
        setGame(data);
        if (data.authorId) {
          getGameAuthor(data.authorId)
            .then((author) => setAuthorName(author.username))
            .catch(() => setAuthorName(null));
        }
        setTitle(data.title || "");
        setDescription(data.description || "");
        setBannerUrl(data.bannerUrl || "");
        setTags(data.gameTags || []);
      } catch (err) {
        setError(err.message || "Не удалось загрузить игру");
      } finally {
        setLoading(false);
      }
    }
    loadGame();
  }, [id, token]);

  function handleAddTag() {
    const trimmed = tagInput.trim();
    if (trimmed && !tags.includes(trimmed)) {
      setTags([...tags, trimmed]);
    }
    setTagInput("");
  }

  function handleRemoveTag(tag) {
    setTags(tags.filter((t) => t !== tag));
  }

  function handleTagKeyDown(e) {
    if (e.key === "Enter") {
      e.preventDefault();
      handleAddTag();
    }
  }

  async function handleUpdate(event) {
    event.preventDefault();
    try {
      setIsSubmitting(true);
      setError("");
      const formData = new FormData();
      formData.append("title", title);
      formData.append("description", description);
      if (bannerUrl) formData.append("bannerUrl", bannerUrl);
      tags.forEach((tag) => formData.append("tags", tag));

      // ЛОГ 1 — что уходит на бэк
      console.log("Отправляем теги:", tags);
      for (let [k, v] of formData.entries()) console.log(k, v);

      const updatedGame = await updateGame(
        id,
        {
          title,
          description,
          bannerUrl: bannerUrl || undefined,
          gameTags: tags,
        },
        token,
      );

      // ЛОГ 2 — что вернул бэк
      // console.log("Ответ бэка:", updatedGame);

      setGame(updatedGame);
      setTitle(updatedGame.title || "");
      setDescription(updatedGame.description || "");
      setBannerUrl(updatedGame.bannerUrl || "");
      setTags(updatedGame.gameTags || []);
      setIsEditing(false);
    } catch (err) {
      setError(err.message || "Не удалось обновить игру");
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleDelete() {
    const confirmed = window.confirm("Уверен? Дороги назад не будет.");
    if (!confirmed) return;
    try {
      setIsSubmitting(true);
      setError("");
      await deleteGame(id, token);
      navigate("/games");
    } catch (err) {
      setError(err.message || "Не удалось удалить игру(к сожалению)");
      setIsSubmitting(false);
    }
  }

  if (loading) return <Loader text="Загрузка игры..." />;
  if (error && !game) return <ErrorState message={error} />;
  if (!game) return <ErrorState message="Игра не найдена))" />;

  return (
    <section className="section-lg">
      <div className="page-header">
        <div className="section">
          <Link to="/games" className="nav-link">
            ← Назад к каталогу
          </Link>
          <h1 className="page-title">{game.title}</h1>
          {authorName && <p className="card-author">{authorName}</p>}
        </div>

        {game.isOwner && (
          <div className="card-actions">
            {!isEditing ? (
              <button
                type="button"
                className="button button-outline"
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
                  setBannerUrl("");
                  setTags(game.gameTags || []);
                  setError("");
                }}
              >
                Отмена
              </button>
            )}
          </div>
        )}
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

            {game.gameTags?.length > 0 && (
              <div className="tag-list">
                {game.gameTags.map((tag) => (
                  <span key={tag} className="tag-badge">
                    {tag}
                  </span>
                ))}
              </div>
            )}
          </div>
        </article>
      ) : (
        <article className="card create-game-card">
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
                onChange={(e) => setTitle(e.target.value)}
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
                onChange={(e) => setDescription(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label className="label" htmlFor="bannerUrl">
                Баннер
              </label>
              <input
                id="bannerUrl"
                className="input"
                type="url"
                placeholder="https://biographe.ru/char/shrek/"
                value={bannerUrl}
                onChange={(e) => setBannerUrl(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label className="label">Теги</label>
              <TagSelector selected={tags} onChange={setTags} />
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
                  setBannerUrl("");
                  setTags(game.gameTags || []);
                  setError("");
                }}
              >
                Отмена
              </button>
            </div>
          </form>
        </article>
      )}
      {isEditing && game.isOwner && (
        <div>
          <button
            type="button"
            className="button button-danger"
            onClick={handleDelete}
            disabled={isSubmitting}
          >
            Удалить игру
          </button>
        </div>
      )}
    </section>
  );
}

export default GamePage;
