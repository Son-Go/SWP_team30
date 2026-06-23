# Grafana Loki

This repository now includes Loki as the log storage backend for the observability overlay.

## What it does

- Runs Loki in single-binary mode for local development.
- Stores log data on a local Docker volume.
- Exposes Loki's HTTP API on `http://localhost:3100`.
- Accepts log pushes from Alloy at `/loki/api/v1/push`.

## Files

- `compose.observability.yaml`
- `observability/loki/config.yaml`

## How it fits

- Alloy discovers Docker containers and forwards logs to Loki.
- Loki stores and serves those logs.
- Grafana can be added later as the query and dashboard UI.

## How to run it

```bash
docker compose -f compose.yaml -f compose.observability.yaml up -d loki alloy
```

## Notes

- This is a local filesystem-backed setup, appropriate for development and debugging.
- The configuration uses a single-replica in-memory ring, which keeps the setup simple for one-machine use.
