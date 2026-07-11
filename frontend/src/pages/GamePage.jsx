import React, { useEffect, useState, useRef } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { deleteGame, getGameById, updateGame, getGameAuthor } from "../api/api";
import ErrorState from "../components/ErrorState";
import Loader from "../components/Loader";
import { useAuth } from "../context/auth-context";
import TagSelector from "../components/TagSelector";
import CommentsSection from "../components/CommentsSection";
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
  const [shortDescription, setShortDescription] = useState("");
  const [bannerUrl, setBannerUrl] = useState("");
  const [gameTags, setGameTags] = useState({
    genre: [],
    town: [],
    stage: [],
    featured: [],
  });
  // const [tagInput, setTagInput] = useState("");
  const [authorName, setAuthorName] = useState(null);

  const [activeScreenshot, setActiveScreenshot] = useState(null);
  const [screenshots, setScreenshots] = useState({ videos: [], pictures: [] });

  const [thumbStart, setThumbStart] = useState(0);

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

  const [showShortDescriptionLimit, setShowShortDescriptionLimit] =
    useState(false);

  const shortDescriptionLimitTimerRef = useRef(null);

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
        setShortDescription(data.shortDescription || "");
        setDescription(data.description || "");
        setBannerUrl(data.bannerUrl || "");
        setGameTags(
          data.gameTags || { genre: [], town: [], stage: [], featured: [] },
        );
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

  // function handleAddTag() {
  //   const trimmed = tagInput.trim();
  //   if (trimmed && !tags.includes(trimmed)) {
  //     setTags([...tags, trimmed]);
  //   }
  //   setTagInput("");
  // }

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
          shortDescription: shortDescription.trim() || null,
          bannerUrl: bannerUrl || undefined,
          gameTags,
          screenshots,
        },
        token,
      );

      const freshGame = await getGameById(id, token);

      setGame(freshGame);
      setTitle(freshGame.title || "");
      setShortDescription(freshGame.shortDescription || "");
      setDescription(freshGame.description || "");
      setBannerUrl(freshGame.bannerUrl || "");
      setGameTags(
        freshGame.gameTags || { genre: [], town: [], stage: [], featured: [] },
      );
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

  const visibleThumbsCount = 7;
  const currentIndex = currentMedia ? orderedMedia.indexOf(currentMedia) : 0;
  const maxThumbStart =
    orderedMedia.length === 0
      ? 0
      : Math.floor((orderedMedia.length - 1) / visibleThumbsCount) *
        visibleThumbsCount;
  const safeThumbStart = Math.min(thumbStart, maxThumbStart);

  function setActiveMediaByIndex(index) {
    if (!orderedMedia.length) return;

    const clampedIndex = Math.max(0, Math.min(index, orderedMedia.length - 1));
    const nextPageStart =
      Math.floor(clampedIndex / visibleThumbsCount) * visibleThumbsCount;

    setActiveScreenshot(orderedMedia[clampedIndex]);
    setThumbStart(nextPageStart);
  }

  const visibleThumbs = orderedMedia.slice(
    safeThumbStart,
    safeThumbStart + visibleThumbsCount,
  );

  // useEffect(() => {
  //   setThumbStart(0);
  // }, [game?.id, orderedMedia.length]);

  const TAG_CATEGORY_CLASSES = {
    genre: "tag-badge-genre",
    town: "tag-badge-town",
    stage: "tag-badge-stage",
    featured: "tag-badge-featured",
  };

  const visibleGameTags = game.gameTags
    ? Object.entries(game.gameTags)
        .filter(([category]) => category !== "featured")
        .flatMap(([category, tags]) =>
          (tags || []).filter(Boolean).map((tag) => ({
            name: tag,
            colorClass: TAG_CATEGORY_CLASSES[category] || "",
          })),
        )
    : [];

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
                  setShortDescription(game.shortDescription || "");
                  setShowShortDescriptionLimit(false);
                  setBannerUrl("");
                  setGameTags(
                    game.gameTags || {
                      genre: [],
                      town: [],
                      stage: [],
                      featured: [],
                    },
                  );
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
        <>
          <div className="game-layout">
            <div className="game-gallery">
              {orderedMedia.length > 0 ? (
                <>
                  <div className="game-gallery-viewer">
                    <button
                      className="gallery-arrow gallery-arrow-left"
                      onClick={() =>
                        setActiveMediaByIndex(
                          currentIndex <= 0
                            ? orderedMedia.length - 1
                            : currentIndex - 1,
                        )
                      }
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
                      onClick={() =>
                        setActiveMediaByIndex(
                          currentIndex >= orderedMedia.length - 1
                            ? 0
                            : currentIndex + 1,
                        )
                      }
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

                  <div className="game-gallery-thumbs-row">
                    <button
                      type="button"
                      className="gallery-arrow-thumb gallery-arrow-thumb-left"
                      onClick={() => setActiveMediaByIndex(currentIndex - 1)}
                      disabled={currentIndex <= 0}
                      aria-label="Показать предыдущие медиа"
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

                    <div className="game-gallery-thumbs">
                      {visibleThumbs.map((url, i) => {
                        const mediaIndex = safeThumbStart + i;

                        return isYoutubeUrl(url) ? (
                          <button
                            key={`${url}-${mediaIndex}`}
                            type="button"
                            className={`game-gallery-thumb game-gallery-thumb-video ${currentMedia === url ? "active" : ""}`}
                            onClick={() => setActiveMediaByIndex(mediaIndex)}
                          >
                            YouTube
                          </button>
                        ) : (
                          <img
                            key={`${url}-${mediaIndex}`}
                            src={url}
                            alt={`${game.title} media ${mediaIndex + 1}`}
                            className={`game-gallery-thumb ${currentMedia === url ? "active" : ""}`}
                            onClick={() => setActiveMediaByIndex(mediaIndex)}
                          />
                        );
                      })}
                    </div>

                    <button
                      type="button"
                      className="gallery-arrow-thumb gallery-arrow-thumb-right"
                      onClick={() => setActiveMediaByIndex(currentIndex + 1)}
                      disabled={currentIndex >= orderedMedia.length - 1}
                      aria-label="Показать следующие медиа"
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
              {game.shortDescription?.trim() && (
                <p className="game-sidebar-short-description">
                  {game.shortDescription}
                </p>
              )}

              {game.createdAt && (
                <div className="game-sidebar-meta">
                  <span className="game-sidebar-label">Дата выхода</span>
                  <span className="card-author">
                    {new Date(game.createdAt).toLocaleDateString("ru-RU", {
                      day: "numeric",
                      month: "long",
                      year: "numeric",
                    })}
                  </span>
                </div>
              )}

              {authorName && (
                <div className="game-sidebar-meta">
                  <span className="game-sidebar-label">Разработчик</span>
                  <span className="card-author">{authorName}</span>
                </div>
              )}

              {visibleGameTags.length > 0 && (
                <div className="game-sidebar-meta">
                  <div className="tag-list">
                    {visibleGameTags.map(({ name, colorClass }) => (
                      <span className={`tag-badge ${colorClass}`} key={name}>
                        {name}
                      </span>
                    ))}
                  </div>
                </div>
              )}
            </aside>
          </div>
          <CommentsSection gameId={id} gameAuthorUsername={authorName} />
        </>
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
                maxLength={50}
                onChange={(e) => setTitle(e.target.value.slice(0, 50))}
                required
              />
            </div>

            <div className="form-group">
              <label className="label" htmlFor="shortDescription">
                Краткое описание
              </label>

              <div className="description-limit-wrap">
                <textarea
                  id="shortDescription"
                  className="textarea"
                  value={shortDescription}
                  placeholder="Кратко расскажите, о чём игра"
                  onChange={(e) => {
                    const value = e.target.value;

                    if (value.length <= 300) {
                      setShortDescription(value);
                      setShowShortDescriptionLimit(false);
                      return;
                    }

                    clearTimeout(shortDescriptionLimitTimerRef.current);
                    setShowShortDescriptionLimit(false);

                    setTimeout(() => {
                      setShowShortDescriptionLimit(true);
                    }, 10);

                    shortDescriptionLimitTimerRef.current = setTimeout(() => {
                      setShowShortDescriptionLimit(false);
                    }, 3000);
                  }}
                />

                {showShortDescriptionLimit && (
                  <span className="input-hint-error description-limit-hint">
                    Достигнут лимит в 300 символов
                  </span>
                )}
              </div>

              <div className="textarea-counter-row">
                <span
                  className={`textarea-counter ${
                    showShortDescriptionLimit ? "limit-hit" : ""
                  }`}
                >
                  {shortDescription.length}/300
                </span>
              </div>
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

              <div className="textarea-counter-row">
                <span
                  className={`textarea-counter ${showLimit ? "limit-hit" : ""}`}
                >
                  {description.length}/1500
                </span>
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
              <TagSelector selected={gameTags} onChange={setGameTags} />
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
                  setShortDescription(game.shortDescription || "");
                  setShowShortDescriptionLimit(false);
                  setBannerUrl("");
                  setGameTags(
                    game.gameTags || {
                      genre: [],
                      town: [],
                      stage: [],
                      featured: [],
                    },
                  );
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
