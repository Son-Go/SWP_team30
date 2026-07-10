import React, { useEffect, useState } from "react";

function GameHoverCard({ game, anchorRect }) {
  const [currentImg, setCurrentImg] = useState(0);
  const pictures = game.pictures || [];

  useEffect(() => {
    setCurrentImg(0);
    if (pictures.length <= 1) return;

    const interval = setInterval(() => {
      setCurrentImg((prev) => (prev + 1) % pictures.length);
    }, 1200);

    return () => clearInterval(interval);
  }, [game.id, pictures.length]);

  if (!anchorRect) return null;

  const viewportWidth = window.innerWidth;
  const popupWidth = 320;
  const gap = 12;

  let left = anchorRect.right + gap;
  if (left + popupWidth > viewportWidth - 8) {
    left = anchorRect.left - popupWidth - gap;
  }

  const top = Math.min(anchorRect.top, window.innerHeight - 400);

  const style = {
    position: "fixed",
    top: Math.max(8, top),
    left,
    width: popupWidth,
    zIndex: 1000,
    pointerEvents: "none",
  };

  const tags =
    game.gameTags && typeof game.gameTags === "object"
      ? Object.entries(game.gameTags)
          .filter(([cat]) => cat !== "featured")
          .flatMap(([cat, list]) =>
            (list || []).map((tag) => ({ name: tag, cat })),
          )
      : (game.tags || []).map((tag) => ({ name: tag, cat: "" }));

  const TAG_CLASS = {
    genre: "tag-badge-genre",
    town: "tag-badge-town",
    stage: "tag-badge-stage",
  };

  const releaseDate = game.releaseDate
    ? new Date(game.releaseDate).toLocaleDateString("ru-RU", {
        day: "numeric",
        month: "short",
        year: "numeric",
      })
    : null;

  return (
    <div className="game-hover-card" style={style}>
      {/* Слайдшоу скриншотов */}
      <div className="game-hover-card__screenshots">
        {pictures.length > 0 ? (
          <>
            <img
              key={currentImg}
              src={pictures[currentImg]}
              alt={`Скриншот ${currentImg + 1}`}
              className="game-hover-card__screenshot"
            />
            {pictures.length > 1 && (
              <div className="game-hover-card__dots">
                {pictures.map((_, i) => (
                  <span
                    key={i}
                    className={`game-hover-card__dot ${i === currentImg ? "active" : ""}`}
                  />
                ))}
              </div>
            )}
          </>
        ) : game.bannerUrl ? (
          <img
            src={game.bannerUrl}
            alt={game.title}
            className="game-hover-card__screenshot"
          />
        ) : (
          <div className="game-hover-card__screenshot game-hover-card__screenshot--empty">
            Нет скриншотов
          </div>
        )}
      </div>

      {/* Инфо */}
      <div className="game-hover-card__body">
        <div className="game-hover-card__title">{game.title}</div>

        {releaseDate && (
          <div className="game-hover-card__date">
            Дата выпуска: {releaseDate}
          </div>
        )}

        {game.shortDescription && (
          <p className="game-hover-card__desc">{game.shortDescription}</p>
        )}

        {tags.length > 0 && (
          <div className="game-hover-card__tags">
            {tags.map(({ name, cat }) => (
              <span key={name} className={`tag-badge ${TAG_CLASS[cat] || ""}`}>
                {name}
              </span>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default GameHoverCard;
