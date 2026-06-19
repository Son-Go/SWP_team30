# PostgreSQL Auto Dump For VM

This project includes a VM-oriented backup system that saves a new PostgreSQL dump only when the database state changes.

## Files

- `scripts/db_scripts/db-dump-on-change-vm.sh`
- `scripts/db_scripts/install-db-dump-timer-vm.sh`

## How it works

1. The script reads the running `postgres` container from `docker compose`.
2. It creates a SQL dump with `pg_dump`.
3. It removes volatile lines that would otherwise change every run.
4. It hashes the normalized dump.
5. It writes a new backup file only if that hash differs from the last saved one.

Backups are stored in:

`backups/postgres/`

The script also maintains:

- `backups/postgres/latest.sql`
- `backups/postgres/.state/last_dump.sha256`
- `backups/postgres/.state/last_dump.txt`

## Manual usage

Start the stack first:

```bash
docker compose up -d
```

Run the backup check:

```bash
bash scripts/db_scripts/db-dump-on-change-vm.sh
```

Force a save even if nothing changed:

```bash
bash scripts/db_scripts/db-dump-on-change-vm.sh --force-save
```

Use a custom folder:

```bash
bash scripts/db_scripts/db-dump-on-change-vm.sh --backup-dir /home/ubuntu/db-backups
```

## Expected output

When a new dump is created:

```text
[SAVE] Database dump saved to /path/to/repo/backups/postgres/postgres_mydatabase_2026-06-19T12-00-00Z.sql
[SAVE] Content hash: <sha256>
```

When the database has not changed:

```text
[SKIP] No database change detected. Last saved dump: /path/to/repo/backups/postgres/postgres_mydatabase_2026-06-19T12-00-00Z.sql
```

When the Postgres container is not running:

```text
[FAIL] Postgres service 'postgres' is not running. Start the stack first with docker compose up -d.
```

## Install automatic periodic backups

This installs a user-level systemd timer that runs every 3 hours by default.

```bash
bash scripts/db_scripts/install-db-dump-timer-vm.sh
```

Custom schedule example:

```bash
bash scripts/db_scripts/install-db-dump-timer-vm.sh --schedule "*-*-* 00/3:00:00"
```

Useful timer commands:

```bash
systemctl --user status swp-team30-db-dump.timer
systemctl --user list-timers swp-team30-db-dump.timer
systemctl --user start swp-team30-db-dump.service
journalctl --user -u swp-team30-db-dump.service --since today
```

## Notes

- The script expects the compose Postgres service to be named `postgres`.
- By default it uses database `mydatabase` and user `myuser`.
- You can override `POSTGRES_SERVICE`, `POSTGRES_DB`, `POSTGRES_USER`, and `POSTGRES_PASSWORD` with environment variables.
- The timer runs every 3 hours by default, and the dump script saves a file only when the database state changed.
- The saved dump is normalized plain SQL so the change detector is stable across repeated runs.
