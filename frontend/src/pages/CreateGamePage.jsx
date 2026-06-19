import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { createGame } from "../api/api";
import ErrorState from "../components/ErrorState";

function CreateGamePage() {
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [banner, setBanner] = useState(null);
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      setSubmitting(true);
      setError("");

      const formData = new FormData();
      formData.append("title", title);
      formData.append("description", description);

      if (banner) {
        formData.append("banner", banner);
      }

      const createdGame = await createGame(formData);
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
          <h1 className="page-title">Создать игру</h1>
        </div>
      </div>

      <article className="card">
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
              Баннер
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
