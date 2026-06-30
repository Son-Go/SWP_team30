ALTER TABLE "users"
    ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(500),
    ADD COLUMN IF NOT EXISTS is_from_tatarstan BOOLEAN NOT NULL DEFAULT false;

UPDATE "users"
SET profile_image_url = ''
WHERE profile_image_url IS NULL;

ALTER TABLE "users"
    ALTER COLUMN profile_image_url SET NOT NULL;

CREATE TABLE IF NOT EXISTS tag_type (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO tag_type(type)
VALUES ('GENRE')
ON CONFLICT (type) DO NOTHING;

ALTER TABLE tag
    ADD COLUMN IF NOT EXISTS tag_type_id INTEGER;

UPDATE tag
SET tag_type_id = (SELECT id FROM tag_type WHERE type = 'GENRE')
WHERE tag_type_id IS NULL;

ALTER TABLE tag
    ALTER COLUMN tag_type_id SET NOT NULL;

ALTER TABLE tag
    DROP CONSTRAINT IF EXISTS fk_tag_tagtype;

ALTER TABLE tag
    ADD CONSTRAINT fk_tag_tagtype FOREIGN KEY (tag_type_id) REFERENCES tag_type(id) ON DELETE RESTRICT;

ALTER TABLE user_role
    DROP CONSTRAINT IF EXISTS fk_userrole_user,
    DROP CONSTRAINT IF EXISTS fk_userrole_role;

ALTER TABLE user_role
    ADD CONSTRAINT fk_userrole_user FOREIGN KEY (user_id) REFERENCES "users"(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_userrole_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE;

ALTER TABLE game_tag
    DROP CONSTRAINT IF EXISTS fk_gametag_game,
    DROP CONSTRAINT IF EXISTS fk_gametag_tag;

ALTER TABLE game_tag
    ADD CONSTRAINT fk_gametag_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_gametag_tag FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE;

ALTER TABLE user_games
    DROP CONSTRAINT IF EXISTS fk_usergames_user,
    DROP CONSTRAINT IF EXISTS fk_usergames_game;

ALTER TABLE user_games
    ADD CONSTRAINT fk_usergames_user FOREIGN KEY (user_id) REFERENCES "users"(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_usergames_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE;

ALTER TABLE order_item
    DROP CONSTRAINT IF EXISTS fk_orderitem_order;

ALTER TABLE order_item
    ADD CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;

ALTER TABLE forum_topic
    DROP CONSTRAINT IF EXISTS fk_forumtopic_category;

ALTER TABLE forum_topic
    ADD CONSTRAINT fk_forumtopic_category FOREIGN KEY (category_id) REFERENCES forum_category(id) ON DELETE CASCADE;

ALTER TABLE forum_post
    DROP CONSTRAINT IF EXISTS fk_forumpost_topic;

ALTER TABLE forum_post
    ADD CONSTRAINT fk_forumpost_topic FOREIGN KEY (topic_id) REFERENCES forum_topic(id) ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_tag_tag_type ON tag(tag_type_id);
