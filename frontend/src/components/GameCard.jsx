import React from "react";
import { Link } from "react-router-dom";

function GameCard({ game }) {
  return (
    <Link to={`/games/${game.id}`} className="card card-link">
      {game.bannerUrl ? (
        <img src={game.bannerUrl} alt={game.title} />
      ) : (
        <div className="state-box">Баннер пока не загружен.</div>
      )}

      <div className="section">
        <h2 className="card-title">{game.title}</h2>
        <p className="card-author">Автор #{game.authorId}</p>

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
