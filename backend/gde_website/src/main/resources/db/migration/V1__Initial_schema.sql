-- Create Users Tables
CREATE TABLE IF NOT EXISTS "users" (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_role (
    user_id BIGINT NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_userrole_user FOREIGN KEY (user_id) REFERENCES "users"(id),
    CONSTRAINT fk_userrole_role FOREIGN KEY (role_id) REFERENCES role(id)
);

-- Create Games Tables
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

CREATE TABLE IF NOT EXISTS tag (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS game_tag (
    game_id BIGINT NOT NULL,
    tag_id INTEGER NOT NULL,
    PRIMARY KEY (game_id, tag_id),
    CONSTRAINT fk_gametag_game FOREIGN KEY (game_id) REFERENCES games(id),
    CONSTRAINT fk_gametag_tag FOREIGN KEY (tag_id) REFERENCES tag(id)
);

CREATE TABLE IF NOT EXISTS user_games (
    user_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    added_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    status VARCHAR(20) DEFAULT 'OWNED',
    PRIMARY KEY (user_id, game_id),
    CONSTRAINT fk_usergames_user FOREIGN KEY (user_id) REFERENCES "users"(id),
    CONSTRAINT fk_usergames_game FOREIGN KEY (game_id) REFERENCES games(id)
);

-- Create Forum Tables
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
    CONSTRAINT fk_forumtopic_category FOREIGN KEY (category_id) REFERENCES forum_category(id),
    CONSTRAINT fk_forumtopic_user FOREIGN KEY (user_id) REFERENCES "users"(id)
);

CREATE TABLE IF NOT EXISTS forum_post (
    id BIGSERIAL PRIMARY KEY,
    topic_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_forumpost_topic FOREIGN KEY (topic_id) REFERENCES forum_topic(id),
    CONSTRAINT fk_forumpost_user FOREIGN KEY (user_id) REFERENCES "users"(id)
);

-- Create Store Tables
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
    CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id) REFERENCES product(id)
);

-- Create Indexes for foreign keys
CREATE INDEX idx_games_author ON games(author_id);
CREATE INDEX idx_game_tag_game ON game_tag(game_id);
CREATE INDEX idx_game_tag_tag ON game_tag(tag_id);
CREATE INDEX idx_user_games_user ON user_games(user_id);
CREATE INDEX idx_user_games_game ON user_games(game_id);
CREATE INDEX idx_user_role_user ON user_role(user_id);
CREATE INDEX idx_user_role_role ON user_role(role_id);
CREATE INDEX idx_forum_topic_category ON forum_topic(category_id);
CREATE INDEX idx_forum_topic_user ON forum_topic(user_id);
CREATE INDEX idx_forum_post_topic ON forum_post(topic_id);
CREATE INDEX idx_forum_post_user ON forum_post(user_id);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_order_item_order ON order_item(order_id);
CREATE INDEX idx_order_item_product ON order_item(product_id);
