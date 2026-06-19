import React from "react";
import { Link } from "react-router-dom";

function GameCard({ game }) {
  return (
    <article className="card section">
      {game.bannerUrl ? (
        <img src={game.bannerUrl} alt={game.title} />
      ) : (
        <div className="state-box">Баннер пока не загружен.</div>
      )}

      <div className="section">
        <h2 className="card-title">{game.title}</h2>

        <p className="card-text">
          {game.description || "Описание пока отсутствует."}
        </p>
      </div>

      <div className="card-actions">
        <Link to={`/games/${game.id}`} className="button">
          К игре
        </Link>
      </div>
    </article>
  );
}

export default GameCard;
