# Mock Database Seed Scripts

These scripts seed the PostgreSQL database with realistic mock data for local development and UI testing.

## Schema Requirement

Run the backend migrations before using these seed scripts. The seed data expects the current schema, including:

- `games.is_approved`
- `games.is_hidden`
- `game_screenshots.is_video`

If seeding fails with an error such as `column "is_approved" of relation "games" does not exist`, your local database schema is behind the current migrations. Recreate or migrate the local database first, then run the seed command again.

## Files

- `01_insert_mock_users.sql`: creates 15 mock users.
- `02_insert_mock_tags.sql`: creates mock tag types and tags.
- `03_insert_mock_games.sql`: creates 50 mock games, assigns them to developers, adds tags, and adds screenshots.
- `04_insert_mock_comments.sql`: adds three comments from different existing users to every game.

The scripts are executed in filename order.

## One-Command Local Run

From the repository root:

```powershell
.\scripts\fill-db.ps1
```

The runner reads `POSTGRES_DB` and `POSTGRES_USER` from `.env.secret`, then streams every `.sql` file in this folder into the Docker Compose `postgres` service.

You can override the database or user:

```powershell
.\scripts\fill-db.ps1 -Database <database> -DbUser <user>
```

## Requirements

- Docker Desktop is running.
- The local Docker Compose `postgres` service is running.
- `.env.secret` contains `POSTGRES_DB` and `POSTGRES_USER`.
- Backend migrations have already been applied to the database.

## Existing Data Behavior

Users and tags use `ON CONFLICT DO NOTHING`, so rerunning those parts does not duplicate the same users or tags.

Games are generated each time `03_insert_mock_games.sql` runs, so rerunning the command adds another batch of mock games.

Comments are generated for every game each time `04_insert_mock_comments.sql` runs, so rerunning the command adds three more comments to each game.

## Manual psql Run

If you are using a local PostgreSQL instance outside Docker Compose:

```bash
psql -U <user> -d <database> -f scripts/db_scripts/mock_scripts/01_insert_mock_users.sql
psql -U <user> -d <database> -f scripts/db_scripts/mock_scripts/02_insert_mock_tags.sql
psql -U <user> -d <database> -f scripts/db_scripts/mock_scripts/03_insert_mock_games.sql
psql -U <user> -d <database> -f scripts/db_scripts/mock_scripts/04_insert_mock_comments.sql
```
