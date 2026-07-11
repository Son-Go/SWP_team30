# Agent Guidance for GDE Website

This document defines safe operating constraints, verification commands, and review expectations for AI coding agents working in this repository.

---

## Repository Overview

- **Backend:** Spring Boot 4 + Java 21, located in `backend/`
- **Frontend:** React 19 + Vite, located in `frontend/`
- **Database:** PostgreSQL, managed via Flyway migrations in `backend/`
- **Infrastructure:** Docker Compose (`compose.yaml`)
- **Docs:** All maintained documentation in `docs/`

---

## Safe Commands

Agents may run these commands freely:

```bash
docker compose up --build
cd backend && mvn test
docker compose ps
docker compose logs
cd frontend && npm run lint
cd frontend && npm run build
```

---

## Constraints and Safety Rules

1. **Never commit secrets.** Do not write to `.env.secret` or hardcode credentials.
2. **Do not modify database migrations.** Flyway files in `backend/src/main/resources/db/migration/` are immutable once applied — add new files instead.
3. **Do not modify `compose.yaml` port bindings** without explicit instruction.
4. **Do not delete or rename public API endpoints** without updating `docs/endpoint-overview.md`.
5. **Do not push directly to `main`.** All changes go through a PR (see [CONTRIBUTING.md](CONTRIBUTING.md)).
6. **Do not modify `CHANGELOG.md` formatting** — follow the existing Keep a Changelog structure.

---

## Verification Steps

After making changes, verify:

```bash
# 1. Application starts without errors
docker compose up --build

# 2. Backend tests pass
cd backend && mvn test   # Expect: BUILD SUCCESS

# 3. Frontend builds
cd frontend && npm run build   # Expect: exit 0, dist/ generated

# 4. Spot-check key endpoint
curl -s http://localhost:8080/api/games | head -c 200
```

---

## Documentation Update Obligations

| Change type | Update required |
|---|---|
| New or modified REST endpoint | `docs/endpoint-overview.md` |
| Dependency or port change | `docs/architecture/README.md`, `README.md` |
| New environment variable | `.env.example`, `docs/development-process.md` |
| Setup or deployment steps | `docs/customer-handover.md`, `README.md` |
| User-visible feature or fix | `CHANGELOG.md` |

---

## Sensitive Areas

- `backend/src/main/resources/db/migration/` — never edit existing Flyway files
- `compose.yaml` — production-aligned infrastructure
- `backend/src/main/java/.../security/` — JWT and authentication logic
- `.env.example` — placeholder values only, never real secrets

---

## Further Reading

- [CONTRIBUTING.md](CONTRIBUTING.md)
- [docs/development-process.md](docs/development-process.md)
- [docs/architecture/README.md](docs/architecture/README.md)
- [docs/customer-handover.md](docs/customer-handover.md)
