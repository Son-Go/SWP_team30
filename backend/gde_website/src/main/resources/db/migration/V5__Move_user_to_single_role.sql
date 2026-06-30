ALTER TABLE "users"
    ADD COLUMN IF NOT EXISTS role VARCHAR(50) NOT NULL DEFAULT 'DEVELOPER';

UPDATE "users" u
SET role = CASE
    WHEN primary_role.name = 'ROLE_ADMIN' THEN 'ADMIN'
    ELSE 'DEVELOPER'
END
FROM (
    SELECT DISTINCT ON (ur.user_id)
        ur.user_id,
        r.name
    FROM user_role ur
    JOIN role r ON r.id = ur.role_id
    ORDER BY ur.user_id, r.id
) primary_role
WHERE u.id = primary_role.user_id;

DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS role;
