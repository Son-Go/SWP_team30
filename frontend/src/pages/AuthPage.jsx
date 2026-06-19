import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import ErrorState from "../components/ErrorState";
import { useAuth } from "../context/auth-context";
import { registerUser } from "../api/api";

function AuthPage() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [mode, setMode] = useState("login");
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      setSubmitting(true);
      setError("");
      setSuccessMessage("");

      if (mode === "login") {
        await login({ username, password });
        navigate("/games");
      } else {
        await registerUser({ username, email, password });
        setSuccessMessage(
          "Регистрация прошла успешно. Теперь войдите в аккаунт.",
        );
        setMode("login");
        setPassword("");
      }
    } catch (err) {
      setError(err.message || "Не удалось выполнить действие");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="page">
      <section
        className="section-lg"
        style={{ maxWidth: "520px", margin: "0 auto" }}
      >
        <div className="section">
          <Link to="/games" className="nav-link">
            ← К каталогу
          </Link>

          <div className="section">
            <h1 className="page-title">
              {mode === "login" ? "Вход" : "Регистрация"}
            </h1>
            <p className="page-subtitle">
              {mode === "login"
                ? "Войдите, чтобы управлять играми и своим контентом."
                : ""}
            </p>
          </div>
        </div>

        <article className="card">
          {error ? <ErrorState message={error} /> : null}

          {successMessage ? (
            <div className="state-box">{successMessage}</div>
          ) : null}

          <form className="form" onSubmit={handleSubmit}>
            {mode === "register" ? (
              <div className="form-group">
                <label className="label" htmlFor="username">
                  Username
                </label>
                <input
                  id="username"
                  className="input"
                  type="text"
                  value={username}
                  onChange={(event) => setUsername(event.target.value)}
                  required
                />
              </div>
            ) : null}

            <div className="form-group">
              <label className="label" htmlFor="email">
                Email
              </label>
              <input
                id="email"
                className="input"
                type="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="label" htmlFor="password">
                Password
              </label>
              <input
                id="password"
                className="input"
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                required
              />
            </div>

            <div className="card-actions">
              <button
                type="submit"
                className="button button-secondary"
                disabled={submitting}
              >
                {submitting
                  ? mode === "login"
                    ? "Вход..."
                    : "Регистрация..."
                  : mode === "login"
                    ? "Войти"
                    : "Зарегистрироваться"}
              </button>

              <button
                type="button"
                className="button button-ghost"
                onClick={() => {
                  setMode(mode === "login" ? "register" : "login");
                  setError("");
                  setSuccessMessage("");
                }}
              >
                {mode === "login"
                  ? "Создать аккаунт"
                  : "Уже есть аккаунт?(удаляй)"}
              </button>
            </div>
          </form>
        </article>
      </section>
    </main>
  );
}

export default AuthPage;
