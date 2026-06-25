import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { getGameAuthor } from "../api/api";

function GameCard({ game }) {
  const [authorName, setAuthorName] = useState(null);

  useEffect(() => {
    if (game.authorId) {
      getGameAuthor(game.authorId)
        .then((data) => setAuthorName(data.username))
        .catch(() => setAuthorName(null));
    }
  }, [game.authorId]);

  return (
    <Link to={`/games/${game.id}`} className="card card-link">
      {game.bannerUrl ? (
        <img src={game.bannerUrl} alt={game.title} />
      ) : (
        <div className="state-box">Баннер пока не загружен.</div>
      )}

      <div className="section">
        <h2 className="card-title">{game.title}</h2>
        {authorName && <p className="card-author">{authorName}</p>}

        {game.tags?.length > 0 && (
          <div className="tag-list">
            {game.tags.map((tag) => (
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
