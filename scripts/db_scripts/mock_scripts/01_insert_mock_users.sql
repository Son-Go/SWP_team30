-- Seed 15 mock users for local development and UI testing.
-- The users table is mapped by UserEntity in the backend.

INSERT INTO "users" (username, email, password_hash, profile_image_url, is_from_tatarstan, role)
VALUES
    ('mock_aurora', 'aurora@example.com', 'mock_hash_aurora', 'https://loremflickr.com/320/240/portrait?random=1', true, 'DEVELOPER'),
    ('mock_benji', 'benji@example.com', 'mock_hash_benji', 'https://loremflickr.com/320/240/portrait?random=2', false, 'DEVELOPER'),
    ('mock_celia', 'celia@example.com', 'mock_hash_celia', 'https://loremflickr.com/320/240/portrait?random=3', true, 'DEVELOPER'),
    ('mock_darian', 'darian@example.com', 'mock_hash_darian', 'https://loremflickr.com/320/240/portrait?random=4', false, 'DEVELOPER'),
    ('mock_elya', 'elya@example.com', 'mock_hash_elya', 'https://loremflickr.com/320/240/portrait?random=5', true, 'DEVELOPER'),
    ('mock_fio', 'fio@example.com', 'mock_hash_fio', 'https://loremflickr.com/320/240/portrait?random=6', false, 'DEVELOPER'),
    ('mock_gale', 'gale@example.com', 'mock_hash_gale', 'https://loremflickr.com/320/240/portrait?random=7', true, 'DEVELOPER'),
    ('mock_hugo', 'hugo@example.com', 'mock_hash_hugo', 'https://loremflickr.com/320/240/portrait?random=8', false, 'DEVELOPER'),
    ('mock_ilan', 'ilan@example.com', 'mock_hash_ilan', 'https://loremflickr.com/320/240/portrait?random=9', true, 'DEVELOPER'),
    ('mock_juno', 'juno@example.com', 'mock_hash_juno', 'https://loremflickr.com/320/240/portrait?random=10', false, 'DEVELOPER'),
    ('mock_kira', 'kira@example.com', 'mock_hash_kira', 'https://loremflickr.com/320/240/portrait?random=11', true, 'DEVELOPER'),
    ('mock_lev', 'lev@example.com', 'mock_hash_lev', 'https://loremflickr.com/320/240/portrait?random=12', false, 'DEVELOPER'),
    ('mock_mira', 'mira@example.com', 'mock_hash_mira', 'https://loremflickr.com/320/240/portrait?random=13', true, 'DEVELOPER'),
    ('mock_nico', 'nico@example.com', 'mock_hash_nico', 'https://loremflickr.com/320/240/portrait?random=14', false, 'DEVELOPER'),
    ('mock_omar', 'omar@example.com', 'mock_hash_omar', 'https://loremflickr.com/320/240/portrait?random=15', true, 'DEVELOPER')
ON CONFLICT (username) DO NOTHING;
