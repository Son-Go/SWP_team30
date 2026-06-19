#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
SYSTEMD_DIR="${HOME}/.config/systemd/user"
SERVICE_FILE="$SYSTEMD_DIR/swp-team30-db-dump.service"
TIMER_FILE="$SYSTEMD_DIR/swp-team30-db-dump.timer"
CHECK_SCRIPT="$REPO_ROOT/scripts/db_scripts/db-dump-on-change-vm.sh"
BACKUP_DIR="$REPO_ROOT/backups/postgres"
ON_CALENDAR="${ON_CALENDAR:-*-*-* 00/3:00:00}"

usage() {
  cat <<'EOF'
Usage:
  bash scripts/db_scripts/install-db-dump-timer-vm.sh [--schedule SYSTEMD_CALENDAR] [--backup-dir PATH]

Defaults:
  schedule   *-*-* 00/3:00:00  (every 3 hours)
  backup dir <repo>/backups/postgres
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --schedule)
      ON_CALENDAR="$2"
      shift 2
      ;;
    --backup-dir)
      BACKUP_DIR="$2"
      shift 2
      ;;
    --help|-h)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      usage
      exit 1
      ;;
  esac
done

mkdir -p "$SYSTEMD_DIR"

cat >"$SERVICE_FILE" <<EOF
[Unit]
Description=SWP Team30 PostgreSQL dump on change

[Service]
Type=oneshot
WorkingDirectory=$REPO_ROOT
ExecStart=/usr/bin/env bash "$CHECK_SCRIPT" --backup-dir "$BACKUP_DIR" --quiet
EOF

cat >"$TIMER_FILE" <<EOF
[Unit]
Description=Run SWP Team30 PostgreSQL dump on change periodically

[Timer]
OnCalendar=$ON_CALENDAR
Persistent=true
Unit=swp-team30-db-dump.service

[Install]
WantedBy=timers.target
EOF

systemctl --user daemon-reload
systemctl --user enable --now swp-team30-db-dump.timer

echo "Installed user timer:"
echo "  $SERVICE_FILE"
echo "  $TIMER_FILE"
echo
echo "Active schedule: $ON_CALENDAR"
echo "Backups directory: $BACKUP_DIR"
echo
echo "Useful commands:"
echo "  systemctl --user status swp-team30-db-dump.timer"
echo "  systemctl --user list-timers swp-team30-db-dump.timer"
echo "  systemctl --user start swp-team30-db-dump.service"
