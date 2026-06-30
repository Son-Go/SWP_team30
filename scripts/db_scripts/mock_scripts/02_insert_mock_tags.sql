-- Seed 25 creative tags for the games catalog.
-- The tag table is mapped by TagEntity and uses tag_type_id as a foreign key.

INSERT INTO tag_type (type)
VALUES ('GENRE')
ON CONFLICT (type) DO NOTHING;

INSERT INTO tag (name, tag_type_id)
VALUES
    ('Neon Drift', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Echo Forge', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Pixel Mirage', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Stormbound', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Velvet Circuit', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Whisper Arbor', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Aurora Relay', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Cinder Bloom', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Sunset Runners', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Tidal Vault', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Gizmo Harbor', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Quantum Orchard', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Rogue Lantern', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Mosaic Frontier', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Brass Echo', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Midnight Tangle', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Phoenix Thread', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Celestial Loop', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Ember Protocol', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Frost Beacon', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Nova Loom', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Cipher Harbor', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Lumen Drift', (SELECT id FROM tag_type WHERE type = 'GENRE')),
    ('Sable Compass', (SELECT id FROM tag_type WHERE type = 'GENRE'))
ON CONFLICT (name) DO NOTHING;
