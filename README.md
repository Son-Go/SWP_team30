# GDE Website

> A community platform for the GDE Innopolis club where indie game developers can publish and manage their projects, while visitors can browse and discover new games.

## 🚀 Quick access

- Product access: [Open the current site](http://gde.maxmir.ru/)
- Hosted documentation: [View the docs site](https://son-go.github.io/SWP_team30/)
- MVP-v0 access: [Open the deployed MVP-v0](https://precious-toffee-52e805.netlify.app/)

## 🎯 Product goal

GDE Website gives local game developers a simple place to share their work and gives the wider community an easy way to discover indie games.

## 📌 Current status

- Status: active development
- Current version: v0.2.5 (MVP-v2.5)
- Current capabilities: user registration and login, browsing games, creating and editing games, and filtering by tags
- Changelog: [CHANGELOG](./CHANGELOG.md)

---

## 🧭 How to use it

- Review the live product at [http://gde.maxmir.ru/](http://gde.maxmir.ru/)
- For customer, TA, or handover review, start with [docs/customer-handover.md](docs/customer-handover.md)
- For a static prototype of the earlier MVP, see [temp-frontend/htmlFrontend](temp-frontend/htmlFrontend)

## 🛠️ Local setup and deployment

For local development or review, start from the handover guide and the repository setup instructions:

```bash
cp .env.example .env.secret # fill in credentials
docker compose up --build
```

Then open [http://localhost:80](http://localhost:80).

---

### High-Level Architecture Summary

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Frontend** | React 19 + Vite + Nginx | Application is served by Nginx, with reverse proxy to backend API |
| **Backend** | Spring Boot 4 + Java 21 | REST API with JWT authentication, JPA/Hibernate ORM, Flyway migrations |
| **Database** | PostgreSQL | Persistent data store for users, games, tags, and relationships |
| **Observability** | Grafana + Prometheus + Loki + Alloy + cAdvisor | Logging, metrics, and monitoring (optional overlay) |


## 📚 Customer and reviewer documentation

The most relevant maintained documents for product review are:

- [docs/customer-handover.md](docs/customer-handover.md) - access, setup, support, troubleshooting, and known limitations
- [docs/architecture/README.md](docs/architecture/README.md) - architecture overview
- [docs/endpoint-overview.md](docs/endpoint-overview.md) - API reference
- [docs/testing.md](docs/testing.md) - test guidance
- [docs/quality-requirements.md](docs/quality-requirements.md) - quality expectations
- [docs/observability.md](docs/observability.md) - monitoring and logging
- [docs/architecture/README.md](docs/architecture/README.md) - architecture overview
- [docs](./docs/) - document folder

## 🤝 Repository guidance

- [CONTRIBUTING.md](CONTRIBUTING.md) - contribution workflow and pull request process
- [AGENTS.md](AGENTS.md) - safe agent operating rules and verification steps
- [CHANGELOG.md](CHANGELOG.md) - recent user-visible changes

---

## 📈 Logging and observability

Project contain several logging tools to log container health and system stability.

List of tools:

- cAdvisor - tracks hardware usage
- Prometheus - logs hardware usage
- Loki - logs docker logs
- Alloy - unifies logs from Prometheus and Loki
- Grafana control panel and UI for all logs

Grafana panel can be accessed on port 3000.

Full observability info: [docs/observability.md](docs/observability.md)

---

## 📝 Weekly README reports

- Week 2 [README.md](./reports/week2/README.md)
- Week 3 [README.md](./reports/week3/README.md)
- Week 4 [README.md](./reports/week4/README.md)
- Week 5 [README.md](./reports/week5/README.md)
- Week 6 [README.md](./reports/week6/README.md)

## 📄 License

See [LICENSE](LICENSE).
