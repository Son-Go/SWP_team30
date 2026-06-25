#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
COMPOSE_FILE="$REPO_ROOT/compose.yaml"
BACKUP_ROOT="$REPO_ROOT/backups/postgres"
STATE_DIR="$BACKUP_ROOT/.state"
TMP_DIR="$(mktemp -d)"

POSTGRES_SERVICE="${POSTGRES_SERVICE:-postgres}"
POSTGRES_DB="${POSTGRES_DB:-mydatabase}"
POSTGRES_USER="${POSTGRES_USER:-myuser}"
POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-secret}"
KEEP_LATEST_LINK=1

cleanup() {
  rm -rf "$TMP_DIR"
}

trap cleanup EXIT

usage() {
  cat <<'EOF'
Usage:
  bash scripts/db_scripts/db-dump-on-change-vm.sh [--backup-dir PATH] [--force-save] [--quiet]

Options:
  --backup-dir PATH  Override the backup output directory
  --force-save       Save a dump even if the database hash did not change
  --quiet            Print only the final result line

Environment overrides:
  POSTGRES_SERVICE
  POSTGRES_DB
  POSTGRES_USER
  POSTGRES_PASSWORD
EOF
}

FORCE_SAVE=0
QUIET=0

while [[ $# -gt 0 ]]; do
  case "$1" in
    --backup-dir)
      BACKUP_ROOT="$2"
      shift 2
      ;;
    --force-save)
      FORCE_SAVE=1
      shift
      ;;
    --quiet)
      QUIET=1
      shift
      ;;
    --help|-h)
      usage
      exit 0
      ;;
    *)
      echo "[FAIL] Unknown argument: $1"
      usage
      exit 1
      ;;
  esac
done

STATE_DIR="$BACKUP_ROOT/.state"
RAW_DUMP="$TMP_DIR/raw_dump.sql"
NORMALIZED_DUMP="$TMP_DIR/normalized_dump.sql"
LAST_HASH_FILE="$STATE_DIR/last_dump.sha256"
LAST_INFO_FILE="$STATE_DIR/last_dump.txt"
TIMESTAMP_UTC="$(date -u +%Y-%m-%dT%H-%M-%SZ)"

log() {
  if [[ "$QUIET" -eq 0 ]]; then
    echo "$1"
  fi
}

fail() {
  echo "[FAIL] $1" >&2
  exit 1
}

require_command() {
  command -v "$1" >/dev/null 2>&1 || fail "Required command not found: $1"
}

normalize_dump() {
  local input_file="$1"
  local output_file="$2"

  sed \
    -e '/^-- Dumped from database version /d' \
    -e '/^-- Dumped by pg_dump version /d' \
    -e '/^-- Started on /d' \
    -e '/^-- Completed on /d' \
    -e '/^\\restrict /d' \
    -e '/^\\unrestrict /d' \
    "$input_file" >"$output_file"
}

require_command docker
require_command sha256sum
require_command sed

[[ -f "$COMPOSE_FILE" ]] || fail "compose.yaml was not found at $COMPOSE_FILE"

mkdir -p "$BACKUP_ROOT" "$STATE_DIR"

log "[INFO] Checking Docker Compose postgres service..."
if ! (
  cd "$REPO_ROOT" &&
  docker compose ps --status running "$POSTGRES_SERVICE" >/dev/null 2>&1
); then
  fail "Postgres service '$POSTGRES_SERVICE' is not running. Start the stack first with docker compose up -d."
fi

log "[INFO] Creating PostgreSQL dump from service '$POSTGRES_SERVICE'..."
(
  cd "$REPO_ROOT" &&
  docker compose exec -T \
    -e PGPASSWORD="$POSTGRES_PASSWORD" \
    "$POSTGRES_SERVICE" \
    pg_dump \
      --username="$POSTGRES_USER" \
      --dbname="$POSTGRES_DB" \
      --clean \
      --if-exists \
      --no-owner \
      --no-privileges \
      >"$RAW_DUMP"
) || fail "pg_dump failed"

normalize_dump "$RAW_DUMP" "$NORMALIZED_DUMP"

CURRENT_HASH="$(sha256sum "$NORMALIZED_DUMP" | awk '{print $1}')"
PREVIOUS_HASH=""

if [[ -f "$LAST_HASH_FILE" ]]; then
  PREVIOUS_HASH="$(cat "$LAST_HASH_FILE")"
fi

if [[ "$FORCE_SAVE" -eq 0 && -n "$PREVIOUS_HASH" && "$CURRENT_HASH" == "$PREVIOUS_HASH" ]]; then
  echo "$CURRENT_HASH" >"$LAST_HASH_FILE"
  if [[ -f "$LAST_INFO_FILE" ]]; then
    LAST_SAVED="$(cat "$LAST_INFO_FILE")"
    echo "[SKIP] No database change detected. Last saved dump: $LAST_SAVED"
  else
    echo "[SKIP] No database change detected. No new dump was created."
  fi
  exit 0
fi

OUTPUT_FILE="$BACKUP_ROOT/postgres_${POSTGRES_DB}_${TIMESTAMP_UTC}.sql"
cp "$NORMALIZED_DUMP" "$OUTPUT_FILE"
echo "$CURRENT_HASH" >"$LAST_HASH_FILE"
echo "$OUTPUT_FILE" >"$LAST_INFO_FILE"

if [[ "$KEEP_LATEST_LINK" -eq 1 ]]; then
  ln -sfn "$(basename "$OUTPUT_FILE")" "$BACKUP_ROOT/latest.sql"
fi

echo "[SAVE] Database dump saved to $OUTPUT_FILE"
echo "[SAVE] Content hash: $CURRENT_HASH"

