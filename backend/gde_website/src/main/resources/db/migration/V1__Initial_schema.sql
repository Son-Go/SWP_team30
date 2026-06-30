-- Users
CREATE TABLE IF NOT EXISTS "users" (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    profile_image_url VARCHAR(500) NOT NULL DEFAULT '',
    is_from_tatarstan BOOLEAN NOT NULL DEFAULT false,
    role VARCHAR(50) NOT NULL DEFAULT 'DEVELOPER',
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Games
CREATE TABLE IF NOT EXISTS games (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    banner_url VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_games_author FOREIGN KEY (author_id) REFERENCES "users"(id)
);

CREATE TABLE IF NOT EXISTS tag_type (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO tag_type(type)
VALUES ('GENRE')
ON CONFLICT (type) DO NOTHING;

CREATE TABLE IF NOT EXISTS tag (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    tag_type_id INTEGER NOT NULL,
    CONSTRAINT fk_tag_tagtype FOREIGN KEY (tag_type_id) REFERENCES tag_type(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS game_tag (
    game_id BIGINT NOT NULL,
    tag_id INTEGER NOT NULL,
    PRIMARY KEY (game_id, tag_id),
    CONSTRAINT fk_gametag_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
    CONSTRAINT fk_gametag_tag FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_games (
    user_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    added_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    status VARCHAR(20) DEFAULT 'OWNED',
    PRIMARY KEY (user_id, game_id),
    CONSTRAINT fk_usergames_user FOREIGN KEY (user_id) REFERENCES "users"(id) ON DELETE CASCADE,
    CONSTRAINT fk_usergames_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS game_screenshots (
    id BIGSERIAL PRIMARY KEY,
    game_id BIGINT NOT NULL,
    url VARCHAR(2000) NOT NULL,
    CONSTRAINT fk_gamescreenshots_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
);

-- Forum
CREATE TABLE IF NOT EXISTS forum_category (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS forum_topic (
    id BIGSERIAL PRIMARY KEY,
    category_id INTEGER NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_forumtopic_category FOREIGN KEY (category_id) REFERENCES forum_category(id) ON DELETE CASCADE,
    CONSTRAINT fk_forumtopic_user FOREIGN KEY (user_id) REFERENCES "users"(id)
);

CREATE TABLE IF NOT EXISTS forum_post (
    id BIGSERIAL PRIMARY KEY,
    topic_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_forumpost_topic FOREIGN KEY (topic_id) REFERENCES forum_topic(id) ON DELETE CASCADE,
    CONSTRAINT fk_forumpost_user FOREIGN KEY (user_id) REFERENCES "users"(id)
);

-- Store
CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    image_url VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES "users"(id)
);

CREATE TABLE IF NOT EXISTS order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    price_at_purchase NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id) REFERENCES product(id)
);

-- Events
CREATE TABLE IF NOT EXISTS event (
    id BIGSERIAL PRIMARY KEY,
    picture_url VARCHAR(500),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    place VARCHAR(500),
    time TEXT NOT NULL,
    additional_info TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_games_author ON games(author_id);
CREATE INDEX IF NOT EXISTS idx_tag_tag_type ON tag(tag_type_id);
CREATE INDEX IF NOT EXISTS idx_game_tag_game ON game_tag(game_id);
CREATE INDEX IF NOT EXISTS idx_game_tag_tag ON game_tag(tag_id);
CREATE INDEX IF NOT EXISTS idx_user_games_user ON user_games(user_id);
CREATE INDEX IF NOT EXISTS idx_user_games_game ON user_games(game_id);
CREATE INDEX IF NOT EXISTS idx_game_screenshots_game ON game_screenshots(game_id);
CREATE INDEX IF NOT EXISTS idx_forum_topic_category ON forum_topic(category_id);
CREATE INDEX IF NOT EXISTS idx_forum_topic_user ON forum_topic(user_id);
CREATE INDEX IF NOT EXISTS idx_forum_post_topic ON forum_post(topic_id);
CREATE INDEX IF NOT EXISTS idx_forum_post_user ON forum_post(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_order_item_order ON order_item(order_id);
CREATE INDEX IF NOT EXISTS idx_order_item_product ON order_item(product_id);
CREATE INDEX IF NOT EXISTS idx_event_created_at ON event(created_at);
