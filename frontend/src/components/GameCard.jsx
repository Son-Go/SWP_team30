import React, { useRef, useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { createPortal } from "react-dom";
import GameHoverCard from "./GameHoverCard";

function GameCard({ game }) {
  const TAG_CATEGORY_CLASSES = {
    genre: "tag-badge-genre",
    town: "tag-badge-town",
    stage: "tag-badge-stage",
    featured: "tag-badge-featured",
  };

  const [hovered, setHovered] = useState(false);
  const [anchorRect, setAnchorRect] = useState(null);

  const [currentPicture, setCurrentPicture] = useState(0);
  const pictures = Array.isArray(game.pictures)
    ? game.pictures.filter(Boolean)
    : [];

  const cardRef = useRef(null);
  const leaveTimer = useRef(null);
  const enterTimer = useRef(null);

  const visibleTags =
    game.gameTags && typeof game.gameTags === "object"
      ? Object.entries(game.gameTags)
          .filter(([category]) => category !== "featured")
          .flatMap(([category, tags]) =>
            (tags || []).filter(Boolean).map((tag) => ({
              name: tag,
              colorClass: TAG_CATEGORY_CLASSES[category] || "",
            })),
          )
      : (game.tags || []).map((tag) => ({
          name: tag,
          colorClass: "",
        }));

  function handleMouseEnter() {
    clearTimeout(leaveTimer.current);
    enterTimer.current = setTimeout(() => {
      if (cardRef.current) {
        setAnchorRect(cardRef.current.getBoundingClientRect());
      }
      setHovered(true);
    }, 1000);
  }

  function handleMouseLeave() {
    clearTimeout(enterTimer.current);
    leaveTimer.current = setTimeout(() => {
      setHovered(false);
      setAnchorRect(null);
    }, 120);
  }

  useEffect(() => {
    if (!hovered) return undefined;

    let animationFrameId;

    function syncHoverPosition() {
      if (!cardRef.current) return;

      const rect = cardRef.current.getBoundingClientRect();

      const isOutsideViewport =
        rect.bottom < 0 ||
        rect.top > window.innerHeight ||
        rect.right < 0 ||
        rect.left > window.innerWidth;

      if (isOutsideViewport) {
        setHovered(false);
        setAnchorRect(null);
        return;
      }

      setAnchorRect((previousRect) => {
        if (
          previousRect &&
          previousRect.top === rect.top &&
          previousRect.left === rect.left &&
          previousRect.width === rect.width &&
          previousRect.height === rect.height
        ) {
          return previousRect;
        }

        return rect;
      });

      animationFrameId = window.requestAnimationFrame(syncHoverPosition);
    }

    animationFrameId = window.requestAnimationFrame(syncHoverPosition);

    return () => {
      window.cancelAnimationFrame(animationFrameId);
    };
  }, [hovered]);

  useEffect(() => {
    if (!hovered || pictures.length <= 1) {
      setCurrentPicture(0);
      return undefined;
    }

    const intervalId = setInterval(() => {
      setCurrentPicture((previous) => (previous + 1) % pictures.length);
    }, 1200);

    return () => clearInterval(intervalId);
  }, [hovered, pictures.length]);

  return (
    <>
      <Link
        to={`/games/${game.id}`}
        className="card card-link"
        ref={cardRef}
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
      >
        {pictures.length > 0 ? (
          <img
            key={hovered ? currentPicture : "banner"}
            src={
              hovered ? pictures[currentPicture] : game.bannerUrl || pictures[0]
            }
            alt={game.title}
            className="card-banner card-banner-slider"
          />
        ) : game.bannerUrl ? (
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

          {/* {visibleTags.length > 0 && (
            <div className="tag-list">
              {visibleTags.map(({ name, colorClass }) => (
                <span className={`tag-badge ${colorClass}`} key={name}>
                  {name}
                </span>
              ))}
            </div>
          )} */}
        </div>
      </Link>

      {hovered &&
        createPortal(
          <GameHoverCard game={game} anchorRect={anchorRect} />,
          document.body,
        )}
    </>
  );
}

export default GameCard;
