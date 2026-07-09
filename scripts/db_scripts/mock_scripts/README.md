# Mock database seed scripts

These scripts mirror the Hibernate/JPA model used by the backend and seed the database with realistic mock data for development and UI testing.

## Hibernate-backed schema summary

The persistence layer is defined by the JPA entities under [backend/gde_website/src/main/java/gde/gde_website](backend/gde_website/src/main/java/gde/gde_website):

- UserEntity → maps to the users table. Each user has a username, email, password hash, profile image URL, role and creation timestamp.
- GamesEntity → maps to the games table. A game has an author, title, description, banner URL and timestamps.
- TagEntity and TagTypeEntity → map to tag and tag_type. A tag belongs to a tag type.
- GameTagEntity → maps to game_tag and implements the many-to-many relation between games and tags.
- GameScreenshotEntity → maps to game_screenshot and stores one-to-many screenshot URLs for each game.
- UserGamesEntity → maps to user_games and stores a user’s relation to a game, such as ownership or library state.

## Relationships used by the seed scripts

- One user can author many games.
- One game can have many tags through game_tag.
- One game can have many screenshots through game_screenshot.
- One user can own or link to many games through user_games.

## Files in this folder

- 01_insert_mock_users.sql: creates 15 mock users.
- 02_insert_mock_tags.sql: creates 25 creative mock tags.
- 03_insert_mock_games.sql: creates 50 mock games, assigns them to random developers, adds random tags (1–5), adds screenshots (1–3) and uses loremflickr banner URLs.

## How to run

If you are using a local PostgreSQL instance:

```bash
psql -U <user> -d <database> -f scripts/db_scripts/mock_scripts/01_insert_mock_users.sql
psql -U <user> -d <database> -f scripts/db_scripts/mock_scripts/02_insert_mock_tags.sql
psql -U <user> -d <database> -f scripts/db_scripts/mock_scripts/03_insert_mock_games.sql
```

If you are running PostgreSQL in Docker Compose, use the same files through the container shell, for example:

```bash
docker compose exec postgres psql -U <user> -d <database> -f /path/to/script.sql
```
