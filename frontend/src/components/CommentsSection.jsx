import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createGameComment, getGameComments } from "../api/api";
import { useAuth } from "../context/auth-context";

function formatDate(iso) {
  return new Date(iso).toLocaleString("ru-RU", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function AvatarPlaceholder({ username }) {
  const initial = username?.[0]?.toUpperCase() || "?";

  return (
    <div className="comment-avatar-placeholder" aria-hidden="true">
      {initial}
    </div>
  );
}

function CommentItem({ comment, gameAuthorUsername }) {
  const { author, text, createdAt } = comment;
  const isGameAuthor = author.username === gameAuthorUsername;

  return (
    <article className="comment-card">
      <div className="comment-header">
        {author.profile_image_url ? (
          <img
            src={author.profile_image_url}
            alt={`Аватар ${author.username}`}
            className="comment-avatar"
          />
        ) : (
          <AvatarPlaceholder username={author.username} />
        )}

        <div className="comment-author">
          <a className="comment-author-name" href={`mailto:${author.email}`}>
            {author.username}
          </a>

          {isGameAuthor && (
            <span className="comment-author-role">разработчик</span>
          )}
        </div>
      </div>

      <p className="comment-text">{text}</p>

      <time className="comment-date" dateTime={createdAt}>
        {formatDate(createdAt)}
      </time>
    </article>
  );
}

function CommentsSection({ gameId, gameAuthorUsername }) {
  const navigate = useNavigate();
  const { token } = useAuth();

  const [comments, setComments] = useState([]);
  const [commentText, setCommentText] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [submitError, setSubmitError] = useState("");
  const [isFormOpen, setIsFormOpen] = useState(false);

  useEffect(() => {
    if (!gameId) return;

    loadComments();
  }, [gameId]);

  async function loadComments() {
    try {
      setLoading(true);
      setError("");

      const data = await getGameComments(gameId);
      setComments(data.content || []);
    } catch (err) {
      setError(err.message || "Не удалось загрузить комментарии");
    } finally {
      setLoading(false);
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (!token) {
      navigate("/auth");
      return;
    }

    const text = commentText.trim();

    if (!text) {
      setSubmitError("Введите текст комментария");
      return;
    }

    try {
      setSubmitting(true);
      setSubmitError("");

      await createGameComment(gameId, text, token);
      setCommentText("");

      await loadComments();

      setIsFormOpen(false);
    } catch (err) {
      setSubmitError(err.message || "Не удалось отправить комментарий");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="comments-section">
      <div className="comments-section-header">
        <h2 className="comments-title">Комментарии</h2>

        <button
          type="button"
          className="button button-secondary comment-add-button"
          onClick={() => {
            if (!token) {
              navigate("/auth");
              return;
            }

            setIsFormOpen(true);
          }}
        >
          Оставить комментарий
        </button>
      </div>

      {isFormOpen && (
        <form className="comment-form" onSubmit={handleSubmit}>
          <textarea
            id="comment-text"
            className="textarea comment-textarea"
            value={commentText}
            onChange={(event) => {
              setCommentText(event.target.value);
              setSubmitError("");
            }}
            placeholder="Напишите, что думаете об игре..."
            maxLength={1000}
            required
            autoFocus
          />

          <div className="comment-form-footer">
            <div>
              {submitError && (
                <span className="comment-form-error">{submitError}</span>
              )}
            </div>

            <div className="comment-form-actions">
              <span className="comment-counter">{commentText.length}/1000</span>

              <button
                type="button"
                className="button button-ghost comment-cancel-button"
                onClick={() => {
                  setIsFormOpen(false);
                  setCommentText("");
                  setSubmitError("");
                }}
                disabled={submitting}
              >
                Отмена
              </button>

              <button
                type="submit"
                className="button button-secondary"
                disabled={submitting || !commentText.trim()}
              >
                {submitting ? "Отправка..." : "Отправить"}
              </button>
            </div>
          </div>
        </form>
      )}

      {loading && <p className="comments-empty">Загрузка комментариев...</p>}

      {!loading && error && (
        <p className="comments-empty comments-error">{error}</p>
      )}

      {!loading && !error && comments.length === 0 && (
        <p className="comments-empty">Комментариев пока нет.</p>
      )}

      {!loading && !error && comments.length > 0 && (
        <div className="comments-list">
          {comments.map((comment) => (
            <CommentItem
              key={comment.id}
              comment={comment}
              gameAuthorUsername={gameAuthorUsername}
            />
          ))}
        </div>
      )}
    </section>
  );
}

export default CommentsSection;
