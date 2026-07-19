import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  createGameComment,
  deleteGameComment,
  getCurrentUser,
  getGameComments,
  updateGameComment,
} from "../api/api";
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

function CommentItem({
  comment,
  isOwnComment,
  isGameAuthor,
  onUpdate,
  onDelete,
  isSubmitting,
}) {
  const { author, text, createdAt } = comment;
  const [isEditing, setIsEditing] = useState(false);
  const [editedText, setEditedText] = useState(text);
  const [editError, setEditError] = useState("");

  function handleCancelEdit() {
    setEditedText(text);
    setEditError("");
    setIsEditing(false);
  }

  async function handleSaveEdit(event) {
    event.preventDefault();

    const trimmedText = editedText.trim();

    if (!trimmedText) {
      setEditError("Комментарий не может быть пустым");
      return;
    }

    try {
      setEditError("");
      await onUpdate(comment.id, trimmedText);
      setIsEditing(false);
    } catch (err) {
      setEditError(err.message || "Не удалось обновить комментарий");
    }
  }

  return (
    <article
      className={`comment-card ${isOwnComment ? "comment-card-own" : ""}`}
    >
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
          <Link
            to={`/users/${author.id}`}
            className="comment-author-name comment-author-link"
          >
            {author.username}
          </Link>

          {isGameAuthor && (
            <span className="comment-author-role">разработчик</span>
          )}
        </div>

        {isOwnComment && !isEditing && (
          <div className="comment-actions">
            <button
              type="button"
              className="comment-action-button"
              onClick={() => setIsEditing(true)}
              aria-label="Редактировать комментарий"
              title="Редактировать"
            >
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M12 20h9" />
                <path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L8 18l-4 1 1-4Z" />
              </svg>
            </button>

            <button
              type="button"
              className="comment-action-button comment-delete-button"
              onClick={() => onDelete(comment.id)}
              disabled={isSubmitting}
              aria-label="Удалить комментарий"
              title="Удалить"
            >
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <polyline points="3 6 5 6 21 6" />
                <path d="M19 6l-1 14H6L5 6" />
                <path d="M10 11v6" />
                <path d="M14 11v6" />
                <path d="M9 6V4h6v2" />
              </svg>
            </button>
          </div>
        )}
      </div>

      {isEditing ? (
        <form className="comment-edit-form" onSubmit={handleSaveEdit}>
          <textarea
            className="textarea comment-textarea"
            value={editedText}
            onChange={(event) => {
              setEditedText(event.target.value);
              setEditError("");
            }}
            maxLength={1500}
            autoFocus
          />

          <div className="comment-form-footer">
            {editError ? (
              <span className="comment-form-error">{editError}</span>
            ) : (
              <span className="comment-counter">{editedText.length}/1500</span>
            )}

            <div className="comment-form-actions">
              <button
                type="button"
                className="button button-ghost comment-cancel-button"
                onClick={handleCancelEdit}
                disabled={isSubmitting}
              >
                Отмена
              </button>

              <button
                type="submit"
                className="button button-secondary"
                disabled={isSubmitting || !editedText.trim()}
              >
                {isSubmitting ? "Сохранение..." : "Сохранить"}
              </button>
            </div>
          </div>
        </form>
      ) : (
        <p className="comment-text">{text}</p>
      )}

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
  const [currentUsername, setCurrentUsername] = useState(null);
  const [actionCommentId, setActionCommentId] = useState(null);

  useEffect(() => {
    if (!gameId) return;

    loadComments();
  }, [gameId]);

  useEffect(() => {
    if (!token) {
      setCurrentUsername(null);
      return;
    }

    getCurrentUser(token)
      .then((user) => setCurrentUsername(user.username))
      .catch(() => setCurrentUsername(null));
  }, [token]);

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

  async function handleUpdateComment(commentId, text) {
    try {
      setActionCommentId(commentId);

      await updateGameComment(gameId, commentId, text, token);
      await loadComments();
    } finally {
      setActionCommentId(null);
    }
  }

  async function handleDeleteComment(commentId) {
    const confirmed = window.confirm("Удалить комментарий?");
    if (!confirmed) return;

    try {
      setActionCommentId(commentId);

      await deleteGameComment(gameId, commentId, token);
      await loadComments();
    } catch (err) {
      setError(err.message || "Не удалось удалить комментарий");
    } finally {
      setActionCommentId(null);
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
            maxLength={1500}
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
              <span className="comment-counter">{commentText.length}/1500</span>

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
              isOwnComment={comment.author.username === currentUsername}
              isGameAuthor={comment.author.username === gameAuthorUsername}
              isSubmitting={actionCommentId === comment.id}
              onUpdate={handleUpdateComment}
              onDelete={handleDeleteComment}
            />
          ))}
        </div>
      )}
    </section>
  );
}

export default CommentsSection;
