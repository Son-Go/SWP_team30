# Contributing to GDE Website

This guide covers the development workflow for contributors to the GDE Website project.

---

## Prerequisites

- Docker and Docker Compose
- Java 21 + Maven (for backend development without Docker)
- Node.js 20+ / npm (for frontend development without Docker)
- Git

---

## Local Setup

```bash
git clone https://github.com/Son-Go/SWP_team30.git
cd SWP_team30
cp .env.example .env.secret
# Edit .env.secret — do not commit this file
docker compose up
```

Application: `http://localhost:80`  
Backend API: `http://localhost:8080`

---

## Branch Strategy

- `main` — stable, deployable branch; direct pushes are not permitted
- Feature branches — name as `feature/<short-description>`
- Bug fix branches — name as `fix/<short-description>`

All changes must go through a Pull Request targeting `main`.

---

## Pull Request Process

1. Create a branch from the latest `main`.
2. Implement your change with focused commits.
3. Ensure the application starts cleanly with `docker compose up`.
4. Run tests (see [docs/testing.md](docs/testing.md)).
5. Open a PR with a clear title and description referencing any relevant issue.
6. Request review from at least one team member.
7. Address all review comments before merging.

---

## Commit Message Convention

Use the format:
<type>(<scope>): <short summary>


Types: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`

Examples:

feat(auth): add JWT refresh token endpoint
fix(frontend): correct tag filter reset behaviour
docs(api): update endpoint-overview with new routes

---

## Running Tests

Full test documentation: [docs/testing.md](docs/testing.md)

```bash
# Backend unit and integration tests
cd backend && mvn test
```

---

## Code Style

- **Backend:** Follow standard Java conventions; use Lombok annotations where already established in the codebase.
- **Frontend:** Follow the existing ESLint configuration.
- Do not introduce new dependencies without team discussion.

---

## Environment Files

- Never commit `.env.secret` or any file containing real credentials.
- Keep `.env.example` up to date when adding new environment variables.

---

## Documentation

- Update `docs/` files when changing APIs, architecture, or deployment steps.
- Update `CHANGELOG.md` for any user-visible change.
- Keep `README.md` current if access URLs, setup steps, or key links change.

---

## Further Reading

- [Architecture](docs/architecture/README.md)
- [Development Process](docs/development-process.md)
- [Definition of Done](docs/definition-of-done.md)
- [AGENTS.md](AGENTS.md)
