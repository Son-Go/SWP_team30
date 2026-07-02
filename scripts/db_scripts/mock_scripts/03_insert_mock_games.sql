-- Seed 50 mock games, assign them to random developers and attach tags + screenshots.
-- This follows the GamesEntity / GameTagEntity / GameScreenshotEntity / UserGamesEntity model.

DO $$
DECLARE
    v_user_id BIGINT;
    v_game_id BIGINT;
    v_tag_id INTEGER;
    v_tag_count INTEGER;
    v_screenshot_count INTEGER;
    v_title TEXT;
    v_description TEXT;
    i INTEGER;
    j INTEGER;
    k INTEGER;
    adjectives TEXT[] := ARRAY['Neon', 'Echo', 'Pixel', 'Nova', 'Arcade', 'Midnight', 'Solar', 'Velvet', 'Glacier', 'Rogue', 'Cipher', 'Aurora', 'Drift', 'Quantum', 'Mosaic', 'Sable', 'Cinder', 'Lumen', 'Ember', 'Frost'];
    nouns TEXT[] := ARRAY['Horizon', 'Circuit', 'Forge', 'Rift', 'Sprint', 'Archive', 'Echo', 'Atlas', 'Bastion', 'Lumen', 'Paradox', 'Voyage', 'Mosaic', 'Nexus', 'Harbor', 'Beacon', 'Loop', 'Protocol', 'Compass', 'Orchard'];
BEGIN
    FOR i IN 1..50 LOOP
        SELECT id INTO v_user_id
        FROM "users"
        WHERE role = 'DEVELOPER'
        ORDER BY random()
        LIMIT 1;

        IF v_user_id IS NULL THEN
            CONTINUE;
        END IF;

        v_title := adjectives[1 + floor(random() * array_length(adjectives, 1))::int] || ' ' ||
                   nouns[1 + floor(random() * array_length(nouns, 1))::int] || ' ' || i;
        v_description := 'Mock game generated for local development and UI testing. ' ||
                         'This title was created to populate the catalogue with varied content.';

        INSERT INTO games (author_id, title, description, banner_url, is_approved)
        VALUES (
            v_user_id,
            v_title,
            v_description,
            'https://loremflickr.com/1200/675/abstract?random=' || i,
            true
        )
        RETURNING id INTO v_game_id;

        INSERT INTO user_games (user_id, game_id, status)
        VALUES (v_user_id, v_game_id, 'OWNED')
        ON CONFLICT (user_id, game_id) DO NOTHING;

        v_tag_count := 1 + floor(random() * 5)::int;
        FOR j IN 1..v_tag_count LOOP
            SELECT id INTO v_tag_id
            FROM tag
            ORDER BY random()
            LIMIT 1;

            INSERT INTO game_tag (game_id, tag_id)
            VALUES (v_game_id, v_tag_id)
            ON CONFLICT DO NOTHING;
        END LOOP;

        v_screenshot_count := 1 + floor(random() * 3)::int;
        FOR k IN 1..v_screenshot_count LOOP
            INSERT INTO game_screenshots (game_id, url, is_video)
            VALUES (
                v_game_id,
                'https://loremflickr.com/1200/675/landscape?random=' || (i * 10 + k),
                false
            )
            ON CONFLICT DO NOTHING;
        END LOOP;
    END LOOP;
END $$;
