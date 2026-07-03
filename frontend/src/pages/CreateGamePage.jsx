import React, { useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { createGame } from "../api/api";
import ErrorState from "../components/ErrorState";
import TagSelector from "../components/TagSelector";
import { useAuth } from "../context/auth-context";

function CreateGamePage() {
  const navigate = useNavigate();
  const { token } = useAuth();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [bannerUrl, setBannerUrl] = useState("");
  const [tags, setTags] = useState([]);
  const [screenshots, setScreenshots] = useState([]);
  const [screenshotInput, setScreenshotInput] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [showScreenshotLimit, setShowScreenshotLimit] = useState(false);
  const screenshotLimitTimerRef = useRef(null);

  function triggerScreenshotLimit() {
    clearTimeout(screenshotLimitTimerRef.current);
    setShowScreenshotLimit(false);
    setTimeout(() => setShowScreenshotLimit(true), 10);
    screenshotLimitTimerRef.current = setTimeout(() => {
      setShowScreenshotLimit(false);
    }, 3000);
  }

  function handleAddScreenshot() {
    const trimmed = screenshotInput.trim();

    if (!trimmed || screenshots.includes(trimmed)) {
      setScreenshotInput("");
      return;
    }

    if (screenshots.length >= 10) {
      triggerScreenshotLimit();
      setScreenshotInput("");
      return;
    }

    setScreenshots([...screenshots, trimmed]);
    setScreenshotInput("");
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
          gameTags: tags,
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

          <div className="form-group">
            <label className="label">Скриншоты</label>

            <div style={{ position: "relative" }}>
              <div className="tag-input-row">
                <input
                  className="input"
                  type="url"
                  placeholder="https://example.com/screenshot.png"
                  value={screenshotInput}
                  onChange={(e) => setScreenshotInput(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") {
                      e.preventDefault();
                      handleAddScreenshot();
                    }
                  }}
                />

                <button
                  type="button"
                  className="button button-ghost"
                  onClick={handleAddScreenshot}
                >
                  Добавить
                </button>
              </div>

              {showScreenshotLimit && (
                <span className="input-hint-error">
                  Достигнут лимит в 10 медиа
                </span>
              )}
            </div>

            {screenshots.length > 0 && (
              <div className="screenshots-edit-list">
                {screenshots.map((url, i) => (
                  <div key={i} className="screenshot-edit-item">
                    <img
                      src={url}
                      alt={`Скриншот ${i + 1}`}
                      className="screenshot-thumb"
                    />
                    <span className="screenshot-url">{url}</span>
                    <button
                      type="button"
                      className="button button-danger screenshot-remove"
                      onClick={() =>
                        setScreenshots(
                          screenshots.filter((_, idx) => idx !== i),
                        )
                      }
                    >
                      ×
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
