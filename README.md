# SWP_team30 GDE website

## Description
GDE Website - website for GDE innopolis club, where local game developers can publish their indie games and russian gamer can explore new games

---

## Access instructions

To access deployed MVP-v0 open [link](https://precious-toffee-52e805.netlify.app/)

To access deployed Project open [link](http://gde.maxmir.ru/)

---

## Quick Access

**Production:** [http://gde.maxmir.ru/](http://gde.maxmir.ru/)

**Local setup** (all MVPs after MVP-v0):
```bash
cp .env.example .env.secret   # fill in credentials
docker compose up
```
Application available at `http://localhost:80`.

For MVP-v0 (static only): open `temp-frontend/htmlFrontend/` with the VS Code Live Server extension.

---

## Local setup instructions

**MVP-v0:** Open https://github.com/Son-Go/SWP_team30/tree/8c2e78c353f0c7f9470b3c63cf59da54a8ecf47e/temp-frontend/htmlFrontend folder, and run VS code Live server extension. MVP-v0 has no backend functionality

**Other MVP:** Clone project, copy [.env.example](/.env.example) to `.env.secret`, run docker compose up. `http://localhost:80` will show working website

---

## Weekly README reports

- Week 2 [README.md](https://github.com/Son-Go/SWP_team30/blob/8b765b5d98da638555c6fb2a9f5be49d44b7cc38/reports/week2/README.md)
- Week 3 [README.md](https://github.com/Son-Go/SWP_team30/blob/1214b680442a30e2ae7adfb5d748f33b7229d92b/reports/week3/README.md)
- Week 4 [README.md](https://github.com/Son-Go/SWP_team30/blob/main/reports/week4/README.md)
- Week 5 [README.md](https://github.com/Son-Go/SWP_team30/blob/main/reports/week5/README.md)

---

## Testing

All testing info can be found in:

- [testing.md](/docs/testing.md) - all automated tests
- [endpoint-testing.md](/docs/endpoint-testing.md) - manual endpoint testing
- [user-acceptance-tests.md](/docs/user-acceptance-tests.md) - UAT tests
- [quality-requirements.md](/docs/quality-requirements.md) - qualiy requirements with links to automated tests
- [endpoint-overview](/docs/endpoint-overview.md) - overeview of all endpoints with their bodies and purposes

--- 

## Logging

Project contain several logging tools to log container health and system stability

List of tools:

- cAdvisor - tracks hardware usage
- Prometheus - logs hardware usage
- Loki - logs docker logs
- Alloy - unifies logs from Prometheus and Loki
- Grafana control panel and UI for all logs

Grafana panel can be accessed on port 3000

Full observability info: [observability.md](/docs/observability.md)

---

## Current functionality

Project supports following function:

- account registration
- viewing existing games
- adding new games
- editing your games
- filtering games by tags

---

## Project status
**Active development**

**Version:** v0.2.0 (MVP-v2 status)

[CHANGELOG](./CHANGELOG.md)

## Documentation

| Resource | Link |
|---|---|
| Hosted documentation site | [https://son-go.github.io/SWP_team30/](https://son-go.github.io/SWP_team30/) |
| Customer handover guide | [docs/customer-handover.md](docs/customer-handover.md) |
| Architecture overview | [docs/architecture/README.md](docs/architecture/README.md) |
| API endpoint reference | [docs/endpoint-overview.md](docs/endpoint-overview.md) |
| Testing guide | [docs/testing.md](docs/testing.md) |
| Quality requirements | [docs/quality-requirements.md](docs/quality-requirements.md) |
| Development process | [docs/development-process.md](docs/development-process.md) |

### High-Level Architecture Summary

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Frontend** | React 19 + Vite + Nginx | Application is served by Nginx, with reverse proxy to backend API |
| **Backend** | Spring Boot 4 + Java 21 | REST API with JWT authentication, JPA/Hibernate ORM, Flyway migrations |
| **Database** | PostgreSQL | Persistent data store for users, games, tags, and relationships |
| **Observability** | Grafana + Prometheus + Loki + Alloy + cAdvisor | Logging, metrics, and monitoring (optional overlay) |

---

## Contributing & Agent Guidance

- [CONTRIBUTING.md](CONTRIBUTING.md) — workflow, branch rules, PR process
- [AGENTS.md](AGENTS.md) — AI agent constraints, safe commands, verification steps

---

## License
[LICENCE](https://github.com/Son-Go/SWP_team30/blob/8b765b5d98da638555c6fb2a9f5be49d44b7cc38/LICENSE)

---
