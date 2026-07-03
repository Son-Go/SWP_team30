import React, { useEffect, useState, useRef } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { deleteGame, getGameById, updateGame, getGameAuthor } from "../api/api";
import ErrorState from "../components/ErrorState";
import Loader from "../components/Loader";
import { useAuth } from "../context/auth-context";
import TagSelector from "../components/TagSelector";
import {
  isImageUrl,
  isYoutubeUrl,
  getTotalMediaCount,
  normalizeMedia,
  getYoutubeEmbedUrl,
} from "../utils/media";

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

  const [activeScreenshot, setActiveScreenshot] = useState(null);
  const [screenshots, setScreenshots] = useState({ videos: [], pictures: [] });

  const [videoInput, setVideoInput] = useState("");
  const [pictureInput, setPictureInput] = useState("");

  const [showMediaLimit, setShowMediaLimit] = useState(false);
  const [showVideoTypeError, setShowVideoTypeError] = useState(false);
  const [showPictureTypeError, setShowPictureTypeError] = useState(false);

  const mediaLimitTimerRef = useRef(null);
  const videoTypeTimerRef = useRef(null);
  const pictureTypeTimerRef = useRef(null);

  const [showLimit, setShowLimit] = useState(false);
  const limitTimerRef = useRef(null);

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
        setScreenshots(normalizeMedia(data.screenshots));
        setActiveScreenshot(null);
      } catch (err) {
        setError(err.message || "Не удалось загрузить игру");
      } finally {
        setLoading(false);
      }
    }
    loadGame();
  }, [id, token]);

  function triggerMediaLimit() {
    clearTimeout(mediaLimitTimerRef.current);
    setShowMediaLimit(false);
    setTimeout(() => setShowMediaLimit(true), 10);
    mediaLimitTimerRef.current = setTimeout(() => {
      setShowMediaLimit(false);
    }, 3000);
  }

  function triggerVideoTypeError() {
    clearTimeout(videoTypeTimerRef.current);
    setShowVideoTypeError(false);
    setTimeout(() => setShowVideoTypeError(true), 10);
    videoTypeTimerRef.current = setTimeout(() => {
      setShowVideoTypeError(false);
    }, 3000);
  }

  function triggerPictureTypeError() {
    clearTimeout(pictureTypeTimerRef.current);
    setShowPictureTypeError(false);
    setTimeout(() => setShowPictureTypeError(true), 10);
    pictureTypeTimerRef.current = setTimeout(() => {
      setShowPictureTypeError(false);
    }, 3000);
  }

  function handleAddVideo() {
    const trimmed = videoInput.trim();

    if (!trimmed) return;

    if (!isYoutubeUrl(trimmed)) {
      triggerVideoTypeError();
      return;
    }

    if (getTotalMediaCount(screenshots) >= 10) {
      triggerMediaLimit();
      return;
    }

    if (screenshots.videos.includes(trimmed)) {
      setVideoInput("");
      return;
    }

    setScreenshots({
      ...screenshots,
      videos: [...screenshots.videos, trimmed],
    });
    setVideoInput("");
  }

  function handleAddPicture() {
    const trimmed = pictureInput.trim();

    if (!trimmed) return;

    if (!isImageUrl(trimmed)) {
      triggerPictureTypeError();
      return;
    }

    if (getTotalMediaCount(screenshots) >= 10) {
      triggerMediaLimit();
      return;
    }

    if (screenshots.pictures.includes(trimmed)) {
      setPictureInput("");
      return;
    }

    setScreenshots({
      ...screenshots,
      pictures: [...screenshots.pictures, trimmed],
    });
    setPictureInput("");
  }

  function handleAddTag() {
    const trimmed = tagInput.trim();
    if (trimmed && !tags.includes(trimmed)) {
      setTags([...tags, trimmed]);
    }
    setTagInput("");
  }

  async function handleUpdate(event) {
    event.preventDefault();
    try {
      setIsSubmitting(true);
      setError("");

      await updateGame(
        id,
        {
          title,
          description,
          bannerUrl: bannerUrl || undefined,
          gameTags: tags,
          screenshots,
        },
        token,
      );

      const freshGame = await getGameById(id, token);

      setGame(freshGame);
      setTitle(freshGame.title || "");
      setDescription(freshGame.description || "");
      setBannerUrl(freshGame.bannerUrl || "");
      setTags(freshGame.gameTags || []);
      setScreenshots(normalizeMedia(freshGame.screenshots));
      setVideoInput("");
      setPictureInput("");
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

  const normalizedGameMedia = normalizeMedia(game?.screenshots);
  const orderedMedia = [
    ...normalizedGameMedia.videos,
    ...normalizedGameMedia.pictures,
  ];
  const currentMedia = activeScreenshot ?? orderedMedia[0] ?? null;
  const currentMediaIsVideo = currentMedia ? isYoutubeUrl(currentMedia) : false;

  return (
    <section className="section-lg">
      <div className="page-header">
        <div className="section">
          <Link to="/games" className="nav-link">
            ← Назад к каталогу
          </Link>
          <h1 className="page-title">{game.title}</h1>
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
                  setScreenshots(normalizeMedia(game.screenshots));
                  setVideoInput("");
                  setPictureInput("");
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
        <div className="game-layout">
          <div className="game-gallery">
            {orderedMedia.length > 0 ? (
              <>
                <div className="game-gallery-viewer">
                  <button
                    className="gallery-arrow gallery-arrow-left"
                    onClick={() => {
                      const idx = orderedMedia.indexOf(currentMedia);
                      const prev =
                        (idx - 1 + orderedMedia.length) % orderedMedia.length;
                      setActiveScreenshot(orderedMedia[prev]);
                    }}
                  >
                    <svg
                      width="24"
                      height="24"
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

                  {currentMediaIsVideo ? (
                    <iframe
                      src={getYoutubeEmbedUrl(currentMedia)}
                      title={`${game.title} video`}
                      className="game-gallery-main game-video-frame"
                      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                      allowFullScreen
                    />
                  ) : (
                    <img
                      src={currentMedia}
                      alt={game.title}
                      className="game-gallery-main"
                    />
                  )}

                  <button
                    className="gallery-arrow gallery-arrow-right"
                    onClick={() => {
                      const idx = orderedMedia.indexOf(currentMedia);
                      const next = (idx + 1) % orderedMedia.length;
                      setActiveScreenshot(orderedMedia[next]);
                    }}
                  >
                    <svg
                      width="24"
                      height="24"
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

                <div className="game-gallery-thumbs">
                  {orderedMedia.map((url, i) =>
                    isYoutubeUrl(url) ? (
                      <button
                        key={i}
                        type="button"
                        className={`game-gallery-thumb game-gallery-thumb-video ${currentMedia === url ? "active" : ""}`}
                        onClick={() => setActiveScreenshot(url)}
                      >
                        YouTube
                      </button>
                    ) : (
                      <img
                        key={i}
                        src={url}
                        className={`game-gallery-thumb ${currentMedia === url ? "active" : ""}`}
                        onClick={() => setActiveScreenshot(url)}
                      />
                    ),
                  )}
                </div>
              </>
            ) : game.bannerUrl ? (
              <img
                src={game.bannerUrl}
                alt={game.title}
                className="game-gallery-main"
              />
            ) : (
              <div className="state-box game-gallery-main">
                Медиа не загружены.
              </div>
            )}

            <div className="game-description">
              <h2 className="card-title">Описание</h2>
              <p className="card-text" style={{ whiteSpace: "pre-wrap" }}>
                {game.description || "Описания нема."}
              </p>
            </div>
          </div>

          <aside className="game-sidebar">
            {game.bannerUrl && (
              <img
                src={game.bannerUrl}
                alt={game.title}
                className="game-sidebar-cover"
              />
            )}
            {authorName && (
              <div className="game-sidebar-meta">
                <span className="game-sidebar-label">Разработчик</span>
                <span className="card-author">{authorName}</span>
              </div>
            )}
            {game.createdAt && (
              <div className="game-sidebar-meta">
                <span className="game-sidebar-label">Дата создания</span>
                <span className="card-author">
                  {new Date(game.createdAt).toLocaleDateString("ru-RU", {
                    day: "numeric",
                    month: "long",
                    year: "numeric",
                  })}
                </span>
              </div>
            )}
            {game.gameTags?.length > 0 && (
              <div className="game-sidebar-meta">
                <div className="tag-list">
                  {game.gameTags.map((tag) => (
                    <Link
                      key={tag}
                      to={`/games?tags=${encodeURIComponent(tag)}`}
                      className="tag-badge tag-badge-selectable"
                    >
                      {tag}
                    </Link>
                  ))}
                </div>
              </div>
            )}
          </aside>
        </div>
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
              <div className="description-limit-wrap">
                <textarea
                  id="description"
                  className="textarea"
                  value={description}
                  onChange={(e) => {
                    if (e.target.value.length <= 1500) {
                      setDescription(e.target.value);
                      setShowLimit(false);
                    } else {
                      clearTimeout(limitTimerRef.current);
                      setShowLimit(false);
                      setTimeout(() => setShowLimit(true), 10);
                      limitTimerRef.current = setTimeout(
                        () => setShowLimit(false),
                        3000,
                      );
                    }
                  }}
                />
                {showLimit && (
                  <span className="input-hint-error description-limit-hint">
                    Достигнут лимит в 1500 символов
                  </span>
                )}
              </div>
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

            <div className="form-group">
              <label className="label">Видео</label>

              <div style={{ position: "relative" }}>
                <div className="tag-input-row">
                  <input
                    className="input"
                    type="url"
                    placeholder="https://youtube.com/watch?v=..."
                    value={videoInput}
                    onChange={(e) => setVideoInput(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === "Enter") {
                        e.preventDefault();
                        handleAddVideo();
                      }
                    }}
                  />

                  <button
                    type="button"
                    className="button button-ghost"
                    onClick={handleAddVideo}
                  >
                    Добавить видео
                  </button>
                </div>

                {showVideoTypeError && (
                  <span className="input-hint-error">
                    Можно добавить только ссылку на YouTube-видео
                  </span>
                )}

                {showMediaLimit && (
                  <span className="input-hint-error">
                    Достигнут лимит в 10 медиа
                  </span>
                )}
              </div>

              {screenshots.videos.length > 0 && (
                <div className="screenshots-edit-list">
                  {screenshots.videos.map((url, i) => (
                    <div key={url} className="screenshot-edit-item">
                      <span className="screenshot-url">{url}</span>
                      <button
                        type="button"
                        className="button button-danger screenshot-remove"
                        onClick={() =>
                          setScreenshots({
                            ...screenshots,
                            videos: screenshots.videos.filter(
                              (_, idx) => idx !== i,
                            ),
                          })
                        }
                      >
                        ✕
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="form-group">
              <label className="label">Скриншоты</label>

              <div style={{ position: "relative" }}>
                <div className="tag-input-row">
                  <input
                    className="input"
                    type="url"
                    placeholder="https://example.com/screenshot.png"
                    value={pictureInput}
                    onChange={(e) => setPictureInput(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === "Enter") {
                        e.preventDefault();
                        handleAddPicture();
                      }
                    }}
                  />

                  <button
                    type="button"
                    className="button button-ghost"
                    onClick={handleAddPicture}
                  >
                    Добавить скриншот
                  </button>
                </div>

                {showPictureTypeError && (
                  <span className="input-hint-error">
                    Можно добавить только ссылку на изображение
                  </span>
                )}

                {showMediaLimit && (
                  <span className="input-hint-error">
                    Достигнут лимит в 10 медиа
                  </span>
                )}
              </div>

              {screenshots.pictures.length > 0 && (
                <div className="screenshots-edit-list">
                  {screenshots.pictures.map((url, i) => (
                    <div key={url} className="screenshot-edit-item">
                      <img src={url} className="screenshot-thumb" />
                      <span className="screenshot-url">{url}</span>
                      <button
                        type="button"
                        className="button button-danger screenshot-remove"
                        onClick={() =>
                          setScreenshots({
                            ...screenshots,
                            pictures: screenshots.pictures.filter(
                              (_, idx) => idx !== i,
                            ),
                          })
                        }
                      >
                        ✕
                      </button>
                    </div>
                  ))}
                </div>
              )}
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
                  setScreenshots(normalizeMedia(game.screenshots));
                  setVideoInput("");
                  setPictureInput("");
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
