# CI Pipeline and Tests Guide

This document describes the current GitHub Actions CI pipeline, what each job checks, and what the automated tests verify.

## Workflow Files

- Main CI workflow: [`.github/workflows/ci.yml`](../.github/workflows/ci.yml)
- Link checker workflow: [`.github/workflows/lychee.yml`](../.github/workflows/lychee.yml)
- Link checker config: [`.github/workflows/lychee.toml`](../.github/workflows/lychee.toml)

The main CI workflow runs on pushes and pull requests targeting `main` or `master`.

## Required Secret

The main CI workflow expects a GitHub Actions secret named `ENV_SECRET_FILE`.

This secret must contain the full raw contents of the project `.env.secret` file. CI recreates `.env.secret` on the runner and uses it for:

- PostgreSQL credentials
- backend datasource configuration
- JWT secret configuration
- Docker Compose smoke testing

## Pipeline Overview

The main CI pipeline is split into these jobs:

1. `Detect changes`
2. `Frontend lint`
3. `Frontend test`
4. `Frontend build`
5. `Backend build and test`
6. `Docker Compose smoke`

There is also a separate `Lychee link check` workflow for documentation links.

## Change Detection

Job name: `Detect changes`

This job uses `dorny/paths-filter` to decide which parts of the pipeline need to run.

It detects changes in:

- `frontend/**`
- `backend/**`
- infrastructure files such as `.github/workflows/**`, `compose.yaml`, Dockerfiles, nginx config, and `scripts/**`

Why it exists:

- frontend-only changes do not need backend verification
- backend-only changes do not need frontend lint/build/test
- infrastructure changes run both sides because they can affect the full stack

## Frontend Lint

Job name: `Frontend lint`

Runs when frontend or infrastructure files changed.

What it does:

```bash
cd frontend
npm ci
npm run lint
```

What it checks:

- JavaScript/React code follows the ESLint rules in [`frontend/eslint.config.js`](../frontend/eslint.config.js)
- obvious syntax, import, hook, and style problems are caught before build time

Pass means ESLint completed successfully. Fail means ESLint found at least one blocking issue.

## Frontend Test

Job name: `Frontend test`

Runs when frontend or infrastructure files changed.

What it does:

```bash
cd frontend
npm ci
npm test
```

The frontend test command runs Vitest.

### `api.test.js`

File: [`frontend/src/test/api.test.js`](../frontend/src/test/api.test.js)

This test file checks the frontend API client.

It verifies that the API client:

- stores and clears the session token in `localStorage`
- sends `GET /games?page={page}` to the configured API base URL
- adds `Authorization: Bearer ...` for authenticated game requests
- sends login credentials as JSON to `POST /auth/login`
- surfaces backend error messages from failed responses
- supports registration through `POST /auth/register`
- loads the authenticated user through `GET /auth/me`
- sends authenticated `DELETE /games/{id}` requests

These tests mock `fetch`, so they do not contact the real backend. Their purpose is to protect the frontend request contract.

### `useGames.test.jsx`

File: [`frontend/src/test/useGames.test.jsx`](../frontend/src/test/useGames.test.jsx)

This test file checks the `useGames` hook.

It verifies that the hook:

- loads the first page of games on mount
- exposes the loaded games to the UI
- tracks whether more pages are available
- appends only unique games when loading more pages
- exposes request errors so pages can render an error state

These tests mock the API module, so they focus on hook behavior rather than backend availability.

## Frontend Build

Job name: `Frontend build`

Runs after frontend lint and frontend tests pass.

What it does:

```bash
cd frontend
npm ci
VITE_API_URL=/api npm run build
```

What it checks:

- the Vite app compiles in production mode
- imports and exports are valid
- React code is buildable
- the configured API base path works at build time

On success, CI uploads the generated `frontend/dist` directory as the `frontend-dist` artifact.

Pass means a production frontend bundle was created. Fail means the app cannot compile as configured in CI.

## Backend Build and Test

Job name: `Backend build and test`

Runs when backend or infrastructure files changed.

What it does:

1. Sets up Java 21.
2. Recreates `.env.secret` from the `ENV_SECRET_FILE` GitHub secret.
3. Loads backend environment variables from `.env.secret`.
4. Starts a PostgreSQL 16 container.
5. Waits until PostgreSQL is healthy.
6. Runs Maven verification:

```bash
cd backend/gde_website
./mvnw -B verify
```

The job uploads:

- Maven Surefire reports from `backend/gde_website/target/surefire-reports`
- Maven Failsafe reports from `backend/gde_website/target/failsafe-reports`
- PostgreSQL logs

Pass means the backend compiled and all Maven verification checks passed. Fail usually means compilation failed, the Spring test context failed to start, PostgreSQL was unavailable, or a JUnit test failed.

## Backend Tests

Backend tests live under:

```text
backend/gde_website/src/test/java
```

Maven automatically includes these tests in `test` and `verify`.

### Controller Unit Tests

Files:

- [`GamesControllerTest.java`](../backend/gde_website/src/test/java/gde/gde_website/games/controller/GamesControllerTest.java)
- [`UsersControllerTest.java`](../backend/gde_website/src/test/java/gde/gde_website/users/controller/UsersControllerTest.java)

These tests instantiate controller classes directly with mocked services.

`GamesControllerTest` verifies:

- listing games returns HTTP `200`
- fetching a game by id passes the authenticated user id to the service
- creating a game without authentication is rejected
- creating a game with authentication returns HTTP `201`

`UsersControllerTest` verifies:

- login returns HTTP `200` and a token response
- `/auth/me` returns HTTP `401` without authentication
- `/auth/me` returns HTTP `200` and current user data with authentication

These are fast unit tests. They check controller behavior, but they do not test real HTTP routing or the Spring Security filter chain.

### HTTP Integration Tests

File: [`EndpointHttpIntegrationTest.java`](../backend/gde_website/src/test/java/gde/gde_website/EndpointHttpIntegrationTest.java)

These tests use `MockMvc` with the Spring MVC and Spring Security layers loaded.

They verify real HTTP behavior for key endpoints:

- `POST /auth/register` returns `201` and a token JSON response
- `POST /auth/login` returns `200` and a token JSON response
- `GET /auth/me` returns `401` without a JWT
- `GET /auth/me` returns `200` with user JSON when a valid JWT is provided
- `GET /games` is public and returns paginated game JSON
- `GET /games/{id}` is public and returns detailed game JSON
- `POST /games` returns `401` without a JWT
- `POST /games` returns `201` and uses the user id from the JWT
- `PATCH /games/{id}` returns `200` and uses the user id from the JWT
- `DELETE /games/{id}` returns `204` and uses the user id from the JWT
- `GET /games/tags/all` returns available tags as JSON

These tests mock the service layer, so they do not require seeded database data. They are intended to catch mistakes in:

- request mappings
- HTTP methods
- status codes
- JSON request/response contracts
- JWT authentication wiring
- Spring Security access rules

They do not test repository queries or real database persistence.

## Docker Compose Smoke

Job name: `Docker Compose smoke`

Runs after frontend and backend checks pass when frontend, backend, or infrastructure files changed.

What it does:

```bash
docker compose up -d --build
docker compose ps
docker compose logs --no-color
docker compose down -v
```

CI also waits for the `backend` and `frontend` containers to become `healthy` or `running`.

What it checks:

- Docker images can build
- the stack can start from `compose.yaml`
- PostgreSQL, backend, and frontend container wiring is valid enough for startup
- backend and frontend services do not crash immediately

This is a smoke test. It does not click through the website or call every API endpoint. Endpoint behavior is covered by backend tests; frontend request behavior is covered by frontend tests.

On failure, inspect the uploaded `docker-compose-logs` artifact.

## Lychee Link Check

Workflow name: `Lychee link check`

What it does:

- scans repository files for broken links
- uses [`.github/workflows/lychee.toml`](../.github/workflows/lychee.toml)

Why it exists:

- prevents broken documentation links from accumulating
- checks README, docs, reports, and other linked files according to the Lychee config

Pass means scanned links resolved or were accepted by config. Fail means at least one link was broken, malformed, timed out, or blocked in a way Lychee treats as an error.

## Running Checks Locally

### Frontend Lint

```powershell
cd frontend
npm ci
npm run lint
cd ..
```

### Frontend Tests

```powershell
cd frontend
npm ci
npm test
cd ..
```

### Frontend Build

```powershell
cd frontend
$env:VITE_API_URL="/api"
npm run build
cd ..
```

### Backend Tests

For unit and HTTP integration tests:

```powershell
cd backend\gde_website
.\mvnw.cmd test
cd ..\..
```

For the same Maven phase used in CI:

```powershell
cd backend\gde_website
$env:JWT_SECRET="01234567890123456789012345678901"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/your_db"
$env:SPRING_DATASOURCE_USERNAME="your_user"
$env:SPRING_DATASOURCE_PASSWORD="your_password"
$env:SPRING_DOCKER_COMPOSE_ENABLED="false"
.\mvnw.cmd -B verify
cd ..\..
```

The HTTP integration tests mock services and do not require database data, but the full backend verification may still need valid datasource configuration because the project contains database-backed Spring Boot configuration.

### Docker Compose Smoke

```powershell
docker compose up -d --build
docker compose ps
docker compose logs backend
docker compose logs frontend
docker compose down -v
```

## Reading Failures

### Frontend Lint Failed

Likely causes:

- ESLint rule violation
- invalid hook usage
- unused or invalid import depending on configured rules

Inspect:

- `Frontend lint` workflow logs
- the file and line reported by ESLint

### Frontend Test Failed

Likely causes:

- API client request shape changed
- hook state behavior changed
- mocked response shape no longer matches code expectations

Inspect:

- `Frontend test` workflow logs
- failing test name in Vitest output
- [`frontend/src/test/api.test.js`](../frontend/src/test/api.test.js)
- [`frontend/src/test/useGames.test.jsx`](../frontend/src/test/useGames.test.jsx)

### Frontend Build Failed

Likely causes:

- missing import or export
- syntax error
- invalid Vite environment variable usage
- dependency problem

Inspect:

- `Frontend build` workflow logs
- Vite error output

### Backend Build and Test Failed

Likely causes:

- Java compilation error
- Spring context startup failure
- PostgreSQL startup or connection problem
- failing JUnit assertion
- changed endpoint contract not reflected in HTTP tests

Inspect:

- `Backend build and test` workflow logs
- first `Caused by:` line in the Maven output
- uploaded `backend-test-reports`
- uploaded `backend-postgres-logs`

### Docker Compose Smoke Failed

Likely causes:

- Docker image build error
- missing or invalid `.env.secret`
- backend cannot connect to PostgreSQL
- backend or frontend container exits early
- health check did not pass in time

Inspect:

- `Docker Compose smoke` workflow logs
- uploaded `docker-compose-logs`
- `compose.yaml`
- backend and frontend Dockerfiles

### Lychee Failed

Likely causes:

- broken link
- moved GitHub resource
- unavailable external URL
- link excluded incorrectly in config

Inspect:

- `Lychee link check` logs
- [`.github/workflows/lychee.toml`](../.github/workflows/lychee.toml)

## Current Coverage Boundaries

The CI pipeline currently gives confidence that:

- frontend code lints
- frontend API client and `useGames` hook behave as expected
- frontend production build succeeds
- backend compiles
- backend controller unit tests pass
- key backend endpoints work through real HTTP routing and security checks
- Docker Compose can build and start the app stack
- documentation links are checked

The CI pipeline does not currently include:

- browser-based end-to-end tests with Playwright or Cypress
- visual regression tests
- real frontend-to-backend user-flow tests
- full repository/database integration tests for every backend service method
- load, performance, or security scanning

These can be added later if the project needs stronger release confidence.
