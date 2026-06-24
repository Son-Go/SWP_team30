# Observability Stack

This project includes an optional observability overlay built around Grafana Alloy, Loki, Prometheus, and Grafana.

## Purpose

The stack is meant to make local Docker-based debugging easier by giving you:

- Centralized logs for all Compose containers.
- Docker container CPU, memory, network, and status metrics.
- Basic metrics for the observability components themselves.
- A single UI for searching logs and viewing metrics.
- A clean path to add backend application metrics later.

## Components

### Alloy

Alloy discovers Docker containers through the Docker socket, reads their logs, adds useful labels, and forwards the logs to Loki.

Files:

- `compose.observability.yaml`
- `observability/alloy/config.alloy`

### Loki

Loki stores logs sent by Alloy and exposes an API that Grafana can query.

Files:

- `compose.observability.yaml`
- `observability/loki/config.yaml`

### Prometheus

Prometheus stores metrics and currently scrapes:

- Prometheus itself
- Alloy
- Loki
- cAdvisor

Files:

- `compose.observability.yaml`
- `observability/prometheus/prometheus.yml`

### cAdvisor

cAdvisor exposes Docker container metrics for Prometheus.

Files:

- `compose.observability.yaml`

### Grafana

Grafana provides the UI and is provisioned automatically with:

- a Prometheus datasource
- a Loki datasource
- a dashboard provider for future JSON dashboards

Files:

- `compose.observability.yaml`
- `observability/grafana/provisioning/datasources/datasources.yaml`
- `observability/grafana/provisioning/dashboards/dashboards.yaml`
- `observability/grafana/dashboards/README.md`

## Exposed Local Endpoints

- App frontend: `http://localhost:80`
- App backend: `http://localhost:8080`
- pgAdmin: `http://localhost:5050`
- Grafana: `http://localhost:3000`
- Loki: `http://localhost:3100`
- Prometheus: `http://localhost:9090`
- Alloy UI: `http://localhost:12345`
- cAdvisor: `http://localhost:8081`

## Start Guide

Start the main application stack first:

```bash
docker compose up -d
```

Start the observability overlay:

```bash
docker compose -f compose.observability.yaml up -d
```

Stop only the observability overlay:

```bash
docker compose -f compose.observability.yaml stop 
```

## CLI Quick Checks

See whether the app and observability containers are up:

```bash
docker compose -f compose.observability.yaml ps
```

Tail logs from the observability services:

```bash
docker compose -f compose.observability.yaml logs 
```

Tail backend logs only:

```bash
docker compose -f compose.observability.yaml logs -f backend
```

## Grafana Login

For local development, Grafana starts with:

- username: `admin`
- password: `admin`

These credentials are defined in `compose.observability.yaml` and should be changed before any non-local use.

## How The Data Flows

1. Docker containers write logs to stdout and stderr.
2. Alloy reads those logs from the Docker socket.
3. Alloy relabels the container metadata.
4. Alloy pushes the logs to Loki.
5. cAdvisor exposes Docker container metrics.
6. Prometheus scrapes metrics from Alloy, Loki, cAdvisor, and Prometheus itself.
7. Grafana queries Loki for logs and Prometheus for metrics.

## How To Work With It

### Check container logs in Grafana

1. Open `http://localhost:3000`.
2. Sign in with `admin` / `admin`.
3. Open **Explore**.
4. Select the `Loki` datasource.
5. Start with a broad query such as:

```logql
{job="docker"}
```

6. Narrow it down with labels such as:

```logql
{job="docker", service_name="backend"}
```

or:

```logql
{job="docker", container_name="swp_team30-frontend-1"}
```

The `service_name` label is usually the easiest one to start with because it comes from Docker Compose service metadata.

### Inspect metrics in Grafana

1. Open **Explore**.
2. Select the `Prometheus` datasource.
3. Try queries such as:

```promql
up
```

```promql
prometheus_target_interval_length_seconds
```

```promql
loki_request_duration_seconds_count
```

Docker container metrics are available from cAdvisor:

```promql
container_last_seen{name=~".*swp_team30.*"}
```

```promql
sum by (name) (rate(container_cpu_usage_seconds_total{name=~".*swp_team30.*", image!=""}[5m]))
```

```promql
sum by (name) (container_memory_usage_bytes{name=~".*swp_team30.*", image!=""})
```

### Inspect metrics in Prometheus directly

Open `http://localhost:9090/targets` to verify scrape health.

This is the fastest way to see whether Prometheus can reach:

- `prometheus:9090`
- `alloy:12345`
- `loki:3100`
- `cadvisor:8080`

### Inspect Alloy directly

Open `http://localhost:12345`.

Use the Alloy UI when you want to confirm:

- the config loaded successfully
- Docker targets were discovered
- the Loki write path is healthy

### Typical local debugging workflow

1. Start the app stack.
2. Start the observability overlay.
3. Open Grafana Explore.
4. Query Loki logs for the `backend` or `frontend` service.
5. Check Prometheus targets if a datasource looks empty.
6. Use the Alloy UI if logs are not reaching Loki.

## Current Limitations

### Backend metrics are not enabled yet

The Spring Boot backend does not currently expose `/actuator/prometheus`, so Prometheus cannot yet scrape JVM and HTTP application metrics from the backend.

Once backend metrics are added, enable the commented scrape job in:

- `observability/prometheus/prometheus.yml`

### Project dashboard

Grafana loads the project dashboard from:

- `observability/grafana/dashboards/swp-local-observability.json`

Open the `Observability` folder in Grafana and choose `SWP Local Observability`.

The dashboard includes:

- overall Docker container CPU usage
- overall Docker container memory usage
- per-container CPU, memory, and network panels
- a container state table based on cAdvisor's last-seen metric
- backend, frontend, Postgres, and all-container log panels
- a dedicated error details panel limited to the last 5 minutes

## File Map

- `compose.yaml`: main application stack
- `compose.observability.yaml`: observability overlay
- `observability/alloy/config.alloy`: Alloy log collection config
- `observability/loki/config.yaml`: Loki config
- `observability/prometheus/prometheus.yml`: Prometheus scrape config
- `observability/grafana/provisioning/datasources/datasources.yaml`: Grafana datasource provisioning
- `observability/grafana/provisioning/dashboards/dashboards.yaml`: Grafana dashboard provisioning
- `observability/grafana/dashboards/swp-local-observability.json`: provisioned project dashboard

## Notes

- This setup is intended for local development and debugging.
- Grafana uses default local credentials for convenience.
- Loki uses a single-binary filesystem-backed configuration.
- Prometheus stores metrics in a local Docker volume.
- Alloy reads Docker logs through the Docker socket.
- cAdvisor reads Docker/container runtime data for local container metrics.
