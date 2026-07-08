import React, { useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { createGame } from "../api/api";
import ErrorState from "../components/ErrorState";
import TagSelector from "../components/TagSelector";
import { useAuth } from "../context/auth-context";
import {
  isYoutubeUrl,
  getTotalMediaCount,
  isImageUrl,
  normalizeMedia,
} from "../utils/media";

function CreateGamePage() {
  const navigate = useNavigate();
  const { token } = useAuth();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [bannerUrl, setBannerUrl] = useState("");
  const [gameTags, setGameTags] = useState({
    genre: [],
    town: [],
    stage: [],
    featured: [],
  });

  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const [showDescriptionLimit, setShowDescriptionLimit] = useState(false);
  const descriptionLimitTimerRef = useRef(null);

  const [screenshots, setScreenshots] = useState({ videos: [], pictures: [] });
  const [videoInput, setVideoInput] = useState("");
  const [pictureInput, setPictureInput] = useState("");

  const [showMediaLimit, setShowMediaLimit] = useState(false);
  const [showVideoTypeError, setShowVideoTypeError] = useState(false);
  const [showPictureTypeError, setShowPictureTypeError] = useState(false);

  const mediaLimitTimerRef = useRef(null);
  const videoTypeTimerRef = useRef(null);
  const pictureTypeTimerRef = useRef(null);

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

  function triggerDescriptionLimit() {
    clearTimeout(descriptionLimitTimerRef.current);
    setShowDescriptionLimit(false);
    setTimeout(() => setShowDescriptionLimit(true), 10);
    descriptionLimitTimerRef.current = setTimeout(() => {
      setShowDescriptionLimit(false);
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

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      setSubmitting(true);
      setError("");

      const createdGame = await createGame(
        {
          title,
          description,
          bannerUrl: bannerUrl || undefined,
          gameTags,
          screenshots,
        },
        token,
      );

      navigate(`/games/${createdGame.id}`);
    } catch (err) {
      setError(err.message || "Не удалось создать игру(Фух...)");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="section-lg">
      <div className="section">
        <Link to="/games" className="nav-link">
          ← Назад
        </Link>
        <div className="section">
          <h1 className="page-title">Выложить игру</h1>
        </div>
      </div>

      <article className="card create-game-card">
        {error ? <ErrorState message={error} /> : null}

        <form className="form" onSubmit={handleSubmit}>
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
                    setShowDescriptionLimit(false);
                  } else {
                    triggerDescriptionLimit();
                  }
                }}
              />
              {showDescriptionLimit && (
                <span className="input-hint-error description-limit-hint">
                  Достигнут лимит в 1500 символов
                </span>
              )}
            </div>

            <div className="textarea-counter-row">
              <span
                className={`textarea-counter ${showDescriptionLimit ? "limit-hit" : ""}`}
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

            <div className="media-input-wrap">
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
                <span className="input-hint-error media-input-hint">
                  Можно добавить только ссылку на YouTube-видео
                </span>
              )}

              {showMediaLimit && (
                <span className="input-hint-error media-input-hint">
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

            <div className="media-input-wrap">
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
                <span className="input-hint-error media-input-hint">
                  Можно добавить только ссылку на изображение
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
              disabled={submitting}
            >
              {submitting ? "Создание..." : "Создать"}
            </button>
            <Link to="/games" className="button button-ghost">
              Отмена
            </Link>
          </div>
        </form>
      </article>
    </section>
  );
}

export default CreateGamePage;
