import React from "react";
import { Link } from "react-router-dom";

function GameCard({ game }) {
  const visibleTags = Array.isArray(game.tags)
    ? game.tags
    : game.gameTags && typeof game.gameTags === "object"
      ? Object.values(game.gameTags).flat().filter(Boolean)
      : [];

  return (
    <Link to={`/games/${game.id}`} className="card card-link">
      {game.bannerUrl ? (
        <img src={game.bannerUrl} alt={game.title} className="card-banner" />
      ) : (
        <div className="state-box card-banner">Баннер пока не загружен.</div>
      )}

      <div className="section">
        <div className="card-author-row">
          {game.author?.username && (
            <>
              {game.author.profile_image_url ? (
                <img
                  src={game.author.profile_image_url}
                  alt={game.author.username}
                  className="card-author-avatar"
                />
              ) : (
                <div className="card-author-avatar card-author-avatar-placeholder">
                  {game.author.username[0].toUpperCase()}
                </div>
              )}
              <div className="card-author-info">
                <span className="card-author">{game.author.username}</span>
                <h2 className="card-title">{game.title}</h2>
              </div>
            </>
          )}
          {!game.author?.username && (
            <h2 className="card-title">{game.title}</h2>
          )}
        </div>

        {visibleTags.length > 0 && (
          <div className="tag-list">
            {visibleTags.map((tag) => (
              <span key={tag} className="tag-badge">
                {tag}
              </span>
            ))}
          </div>
        )}
      </div>
    </Link>
  );
}

export default GameCard;
