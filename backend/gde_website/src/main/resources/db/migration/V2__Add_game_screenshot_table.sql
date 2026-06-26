CREATE TABLE IF NOT EXISTS game_screenshot (
    game_id BIGINT NOT NULL,
    screenshot_url VARCHAR(500) NOT NULL,
    PRIMARY KEY (game_id, screenshot_url),
    CONSTRAINT fk_gamescreenshot_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_game_screenshot_game ON game_screenshot(game_id);
