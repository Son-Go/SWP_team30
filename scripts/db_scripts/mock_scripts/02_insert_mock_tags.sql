-- Seed 25 creative tags for the games catalog.
-- The tag table is mapped by TagEntity and uses tag_type_id as a foreign key.

INSERT INTO tag_type (type)
VALUES ('genre')
ON CONFLICT (type) DO NOTHING;

INSERT INTO tag_type (type)
VALUES ('town')
ON CONFLICT (type) DO NOTHING;

INSERT INTO tag_type (type)
VALUES ('stage')
ON CONFLICT (type) DO NOTHING;

INSERT INTO tag_type (type)
VALUES ('featured')
ON CONFLICT (type) DO NOTHING;

INSERT INTO tag (name, tag_type_id)
VALUES
    ('horror', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('FPS', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Action', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Adventure', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('RPG', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('RTS', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Simulation', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Puzzle', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('TPS', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('MMO', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('MOBA', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Battle Royale', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Stealth', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Survival', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Sandbox', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Open World', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Platformer', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Roguelike', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Metroidvania', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Visual Novel', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Rhythm', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Sport', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Racing', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Fighting', (SELECT id FROM tag_type WHERE type = 'genre')),
    ('Cozy', (SELECT id FROM tag_type WHERE type = 'genre')),

    ('Prototype', (SELECT id FROM tag_type WHERE type = 'stage')),
    ('Demo', (SELECT id FROM tag_type WHERE type = 'stage')),
    ('Release', (SELECT id FROM tag_type WHERE type = 'stage')),

    ('Innopolis', (SELECT id FROM tag_type WHERE type = 'town')),
    ('Kazan', (SELECT id FROM tag_type WHERE type = 'town')),
    ('Tatarstan', (SELECT id FROM tag_type WHERE type = 'town')),
    ('Other', (SELECT id FROM tag_type WHERE type = 'town')),

    ('Featured', (SELECT id FROM tag_type WHERE type = 'featured')),
ON CONFLICT (name) DO NOTHING;
