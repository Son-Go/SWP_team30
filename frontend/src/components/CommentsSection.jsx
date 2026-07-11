import React, { useEffect, useState } from "react";
import { getGameComments } from "../api/api";

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

function CommentItem({ comment }) {
  const { author, text, createdAt } = comment;

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

        <a className="comment-author-name" href={`mailto:${author.email}`}>
          {author.username}
        </a>
      </div>

      <p className="comment-text">{text}</p>

      <time className="comment-date" dateTime={createdAt}>
        {formatDate(createdAt)}
      </time>
    </article>
  );
}

function CommentsSection({ gameId }) {
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!gameId) return;

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

    loadComments();
  }, [gameId]);

  return (
    <section className="comments-section">
      <h2 className="comments-title">Комментарии</h2>

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
            <CommentItem key={comment.id} comment={comment} />
          ))}
        </div>
      )}
    </section>
  );
}

export default CommentsSection;
