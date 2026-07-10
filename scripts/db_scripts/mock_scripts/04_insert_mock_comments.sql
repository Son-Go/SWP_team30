-- Add three mock comments to every game, each written by a different existing user.
-- This follows the CommentEntity model and runs after users and games are seeded.

DO $$
DECLARE
    v_game_id BIGINT;
    v_user_ids BIGINT[];
    v_comment_texts TEXT[] := ARRAY[
        'Really enjoyed the atmosphere and overall presentation.',
        'The core idea is fun, and I would love to see how it develops.',
        'Nice work! The gameplay and visual style fit together well.'
    ];
    i INTEGER;
BEGIN
    IF (SELECT count(*) FROM "users") < 3 THEN
        RAISE EXCEPTION 'At least three users are required to seed comments.';
    END IF;

    FOR v_game_id IN SELECT id FROM games LOOP
        SELECT array_agg(id)
        INTO v_user_ids
        FROM (
            SELECT id
            FROM "users"
            ORDER BY random()
            LIMIT 3
        ) AS selected_users;

        FOR i IN 1..3 LOOP
            INSERT INTO comment (user_id, game_id, text)
            VALUES (v_user_ids[i], v_game_id, v_comment_texts[i]);
        END LOOP;
    END LOOP;
END $$;
