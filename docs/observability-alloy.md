# Grafana Alloy

This repository now includes a first observability overlay for Grafana Alloy.

## What it does

- Runs Alloy as a separate Docker Compose service.
- Reads Docker container logs from the Docker socket.
- Adds useful labels for Compose project, Compose service, and container name.
- Forwards logs to Loki using the `LOKI_PUSH_URL` environment variable.
- Exposes the Alloy UI on `http://localhost:12345`.

## Files

- `compose.observability.yaml`
- `observability/alloy/config.alloy`

## How to run it

Start the main app stack first:

```bash
docker compose up -d
```

Start Loki and Alloy with the observability overlay:

```bash
docker compose -f compose.yaml -f compose.observability.yaml up -d loki alloy
```

## Notes

- The overlay expects the base Compose network to already exist.
- The default Loki push target is `http://loki:3100/loki/api/v1/push`.
- Loki is exposed on `http://localhost:3100`.
