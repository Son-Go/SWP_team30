# CI Tests Guide

This document explains the tests and checks implemented in the GitHub Actions CI workflow, what each one validates, how to run the same checks locally on Windows, and how to understand common failures.

## Where the CI lives

- Main CI workflow: [.github/workflows/ci.yml]
- Link checker workflow: [.github/workflows/lychee.yml]
- Link checker config: [.github/workflows/lychee.toml]

## Secret file requirement

The CI workflow expects a GitHub Actions secret named `ENV_SECRET_FILE`.

What it must contain:
- the full raw contents of the local `.env.secret` file

How CI uses it:
- recreates `.env.secret` on the GitHub runner
- loads PostgreSQL and backend credentials from that file
- uses the same file for the Docker Compose smoke test


## What tests were added

### 1. Detect changes

Job name: `Detect changes`

What it checks:
- Determines whether the current PR or push touched `frontend/**`, `backend/**`, or shared infrastructure files.
- Prevents unrelated jobs from running when only one part of the repo changed.



### 2. Frontend lint

Job name: `Frontend lint`

What it checks:
- Installs frontend dependencies with `npm ci`.
- Runs `npm run lint`.
- Validates the frontend code against the ESLint rules in [frontend/eslint.config.js]

Why it exists:
- Catches basic code quality problems early.
- Prevents style and unused-code issues from growing.

How to understand the result:
- `pass` means ESLint found no blocking errors.
- `fail` means the lint command exited with a non-zero code.
- Warnings do not currently fail this job.


### 3. Frontend build

Job name: `Frontend build`

What it checks:
- Installs frontend dependencies with `npm ci`.
- Sets `VITE_API_URL=http://localhost:8080`.
- Runs `npm run build`.
- Uploads the generated `frontend/dist` artifact on success.

Why it exists:
- Confirms the Vite app can compile in a clean environment.
- Catches missing imports, build-time syntax issues, and invalid environment usage.

How to understand the result:
- `pass` means the production bundle was created successfully.
- `fail` means the frontend cannot compile as configured in CI.


### 4. Backend build and test

Job name: `Backend build and test`

What it checks:
- Recreates `.env.secret` from the `ENV_SECRET_FILE` GitHub secret.
- Starts a PostgreSQL container using credentials from `.env.secret`.
- Sets backend environment variables:
  - `JWT_SECRET`
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
  - `SPRING_DOCKER_COMPOSE_ENABLED=false`
- Runs `./mvnw -B verify` in `backend/gde_website`.
- Uploads Maven test reports from `target/surefire-reports` and `target/failsafe-reports`.

Why it exists:
- Confirms the Spring Boot application can build and run tests in CI.
- Verifies that backend code works with the same secret-based configuration used by the project locally.

How to understand the result:
- `pass` means Maven verification and tests completed successfully.
- `fail` usually means one of these:
  - database connection problem
  - invalid environment variable values
  - application context startup failure
  - failing JUnit tests




### 5. Docker Compose smoke

Job name: `Docker Compose smoke`

What it checks:
- Recreates `.env.secret` from the `ENV_SECRET_FILE` GitHub secret.
- Runs `docker compose up -d --build`.
- Waits for the `backend` and `frontend` containers to become `healthy` or `running`.
- Prints `docker compose ps`.
- Captures `docker compose logs` into `compose.log`.
- Uploads `compose.log` as an artifact.
- Runs `docker compose down -v` during cleanup.

Why it exists:
- Verifies that the full stack can be built and started together.
- Catches Dockerfile problems, startup crashes, and broken container wiring.

How to understand the result:
- `pass` means the containers built and started successfully.
- `fail` means at least one service did not start or did not become healthy in time.
- This job is currently `continue-on-error: true`, so it is informative and non-blocking for now.


### 6. Lychee link check

Job name: `lychee`

What it checks:
- Scans markdown and HTML files for broken links.
- Uses the config in [.github/workflows/lychee.toml]

Why it exists:
- Prevents broken documentation links from accumulating in the repo.

How to understand the result:
- `pass` means scanned links resolved successfully.
- `fail` means one or more links were unreachable, malformed, or timed out.

## How to run the checks locally on Windows

### Frontend lint

```powershell
cd frontend
npm ci
npm run lint
cd ..
```

### Frontend build

```powershell
cd frontend
$env:VITE_API_URL="http://localhost:8080"
npm run build
cd ..
```

### Backend build and test

If your local PostgreSQL already uses the repo credentials:

```powershell
cd backend\gde_website
$env:JWT_SECRET="ci-jwt-secret-value-with-32-characters-minimum"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/mydatabase"
$env:SPRING_DATASOURCE_USERNAME="myuser"
$env:SPRING_DATASOURCE_PASSWORD="secret"
$env:SPRING_DOCKER_COMPOSE_ENABLED="false"
cmd /c mvnw.cmd -B verify
cd ..\..
```

If your local PostgreSQL uses different credentials:
- Either update the environment variables above to match the running database.
- Or stop the current PostgreSQL container/service and start a clean test instance with the CI credentials.

### Docker Compose smoke

```powershell
$env:JWT_SECRET="ci-jwt-secret-value-with-32-characters-minimum"
docker compose up -d --build
docker compose ps
docker compose logs backend
docker compose logs frontend
docker compose down -v
```

## How to read failures quickly

### Frontend lint failed

Likely meaning:
- ESLint found a blocking issue.

What to inspect:
- The workflow log for the `Frontend lint` job.
- The relevant frontend file named in the ESLint output.

### Frontend build failed

Likely meaning:
- The app cannot compile in production mode.

What to inspect:
- Missing environment variables such as `VITE_API_URL`
- Import/export mistakes
- Vite or Node version mismatch

### Backend build and test failed

Likely meaning:
- The Spring context could not start.
- The database credentials or host are wrong.
- A test failed.

What to inspect:
- Job log for the first `Caused by:` line
- Uploaded Maven reports under `backend-test-reports`
- DB settings in:
  - [application.properties]
  - [compose.yaml]
  - [ci.yml]



## Current limitations

- Frontend lint currently allows warnings, so unused imports still pass CI.
- Backend verification currently depends on a PostgreSQL instance accepting the configured credentials.
- Docker Compose smoke is intentionally non-blocking until the stack is consistently stable in CI.
