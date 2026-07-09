ALTER TABLE games
ADD COLUMN IF NOT EXISTS short_description VARCHAR(500) NOT NULL DEFAULT '';

UPDATE games
SET short_description = LEFT(COALESCE(NULLIF(description, ''), title), 500)
WHERE short_description = '';

CREATE TABLE IF NOT EXISTS comment (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    text TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES "users"(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_comment_user ON comment(user_id);
CREATE INDEX IF NOT EXISTS idx_comment_game ON comment(game_id);
