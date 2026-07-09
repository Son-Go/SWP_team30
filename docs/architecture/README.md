# Architecture Documentation — GDE Website

> **Maintained architecture artifact** for the GDE website.
> This document describes the system's static structure, dynamic behavior, and deployment model.

---

## Overview

The system follows a **three-tier architecture** with a clear separation between frontend, backend, and database layers.

### High-Level Architecture Summary

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Frontend** | React 19 + Vite + Nginx | Application is served by Nginx, with reverse proxy to backend API |
| **Backend** | Spring Boot 4 + Java 21 | REST API with JWT authentication, JPA/Hibernate ORM, Flyway migrations |
| **Database** | PostgreSQL | Persistent data store for users, games, tags, and relationships |
| **Observability** | Grafana + Prometheus + Loki + Alloy + cAdvisor | Logging, metrics, and monitoring (optional overlay) |

### Key Design Principles

- **Stateless authentication**: JWT tokens are used for authentication; no server-side sessions.
- **Layered architecture**: Controller → Service → Repository pattern in the backend.
- **Containerized deployment**: All services run as Docker containers orchestrated by Docker Compose.
- **Separation of concerns**: Frontend and backend are independently buildable and deployable.
- **Diagrams-as-code**: All architecture diagrams are maintained as PlantUML source files in the repository.

---

## Technology Stack

### Frontend

- **Framework**: React 19.1 with Vite 7
- **Routing**: React Router DOM 7
- **Testing**: Vitest (unit), Playwright (E2E)
- **Linting**: ESLint 9
- **Build**: Vite (production build → static files)
- **Serving**: Nginx (reverse proxy + static file serving)

### Backend

- **Framework**: Spring Boot 4.0.7 (Java 21)
- **Security**: Spring Security + JWT (jjwt 0.12.6)
- **ORM**: Spring Data JPA / Hibernate
- **Database**: PostgreSQL (via JDBC)
- **Migrations**: Flyway
- **Build**: Maven (with Maven Wrapper)
- **Additional**: Lombok, Spring Boot Starter Mail, Spring Boot Starter Kafka, Spring Boot Starter Validation

### Database

- **PostgreSQL** (latest) with persistent Docker volume
- **Schema management**: Flyway migrations (`classpath:db/migration`)
- **Key tables**: `users`, `games`, `tags`, `game_tags`, `user_games`

### Observability

- **Grafana**: Dashboard UI (port 3000)
- **Prometheus**: Metrics storage (port 9090)
- **Loki**: Log aggregation (port 3100)
- **Alloy**: Log collection from Docker containers (port 12345)
- **cAdvisor**: Container resource metrics (port 8081)

---

## Static View — Component Diagram

### Diagram Source

The component diagram is maintained as a PlantUML file:

[`static-view/component-diagram.puml`](static-view/component-diagram.puml)

![Component Diagram](./static-view/component-diagram.svg)

### What the Diagram Shows

The component diagram illustrates the **internal structure** of the GDE Website system, including:

1. **Frontend components**: React pages (GamesPage, GamePage, CreateGamePage, AuthPage), shared components (NavBar, ProtectedRoute), state management (AuthContext), and the API client (fetch wrapper). Nginx serves as the reverse proxy and static file server.

2. **Backend components**: Spring Boot REST controllers organized by domain module (Games, Users, Forum, Store). Each module follows the Controller → Service → Repository layered pattern. Security is handled by a JWT filter chain (SecurityConfig → JwtFilter → JwtUtils). Flyway manages database migrations.

3. **Database**: PostgreSQL is the sole persistent data store, accessed via JPA/Hibernate through JDBC.

4. **Observability stack**: Grafana, Prometheus, Loki, Alloy, and cAdvisor form a monitoring and logging pipeline. Alloy collects Docker container logs and forwards them to Loki. Prometheus scrapes metrics from Alloy, Loki, and cAdvisor. Grafana provides the unified dashboard UI.

5. **External systems**: pgAdmin provides a database administration UI. The user interacts with the system through a web browser.

6. **Communication paths**: The user's browser communicates with Nginx (port 80), which serves the SPA and reverse-proxies `/api/` requests to the backend (port 8080). The backend communicates with PostgreSQL via JDBC. The observability stack forms its own internal network.

### Coupling and Cohesion Analysis

**Coupling:**

- **Low coupling between frontend and backend**: The frontend communicates with the backend exclusively through a well-defined REST API (JSON over HTTP). The frontend does not depend on backend internals, and the backend does not depend on frontend implementation details. This loose coupling allows independent development and deployment of each layer.
- **Low coupling between modules**: The Games, Users, Forum, and Store modules are independent packages with no direct cross-module dependencies (except GamesService referencing UsersRepository for author lookups). This supports independent evolution of each domain.
- **Moderate coupling between backend and database**: The backend uses JPA/Hibernate for ORM, which creates a direct dependency on the PostgreSQL schema. However, Flyway migrations provide a controlled way to evolve the schema.
- **Low coupling between observability and application**: The observability stack is a separate Docker Compose overlay that connects to the same network but does not modify application code. It reads logs from the Docker socket and metrics from container endpoints.

**Cohesion:**

- **High cohesion within modules**: Each backend module (Games, Users) has a clear set of responsibilities with tightly related classes (Controller, Service, Repository, Entity, Model). The Games module handles all game-related operations, and the Users module handles all authentication and user management.
- **High cohesion in frontend components**: Each React page component is focused on a single user-facing feature (listing games, viewing a game, creating a game, authentication). Shared components (NavBar, Layout, ProtectedRoute) are reusable and well-separated.
- **Moderate cohesion in the API client**: The frontend API client (`api.js`) consolidates all HTTP requests in one place, which provides a single point of change for API communication but may become a bottleneck as the API grows.

### Maintainability Implications

- **Positive**: The layered architecture (Controller → Service → Repository) makes it straightforward to add new endpoints or modify business logic without affecting other layers. The module-based package structure (games, users, forum, store) allows developers to work on different features in parallel.
- **Positive**: The use of Flyway for database migrations ensures that schema changes are version-controlled and reproducible across environments.
- **Positive**: The frontend's use of React Router and component-based architecture makes it easy to add new pages or modify existing ones.
- **Concern**: The frontend API client is a single file that handles all API calls. As the API grows, this may need to be split into domain-specific API modules.
- **Concern**: The backend uses `spring.jpa.hibernate.ddl-auto=update` in development, which can lead to schema drift. Flyway is enabled but the `ddl-auto` setting should be set to `validate` or `none` in production.

### Quality Requirements Supported

| Quality Requirement | How the Structure Supports It |
|---------------------|-------------------------------|
| **Security (QR-001, QR-002)** | JWT authentication with Spring Security filter chain. Stateless sessions. BCrypt password hashing. Endpoints are explicitly secured or permitted. |
| **Functional Suitability (QR-003)** | Public endpoints (GET /games, GET /games/author/{id}) are accessible without authentication. The API contract is stable and well-defined. |
| **Maintainability** | Layered architecture, module-based packages, and diagrams-as-code approach make the system easy to understand and modify. |
| **Scalability** | Stateless backend and containerized deployment allow horizontal scaling. PostgreSQL can be scaled with read replicas. |
| **Observability** | The optional observability stack provides centralized logging and metrics, supporting debugging and monitoring. |

---

## Architecture Decisions and Traceability

The architecture views in this document are complemented by the ADRs in [adr/ADR-001.md](adr/ADR-001.md), [adr/ADR-002.md](adr/ADR-002.md), [adr/ADR-003.md](adr/ADR-003.md), [adr/ADR-004.md](adr/ADR-004.md), and [adr/ADR-005.md](adr/ADR-005.md). Together, they explain both the structure of the system and the reasoning behind the key choices.

- [ADR-001](adr/ADR-001.md) captures the decision to use JWT-based authentication for the protected write operations that are covered by [QR-001](../quality-requirements.md#qr-001-patch-gamesid-security-enforcement) and [QR-002](../quality-requirements.md#qr-002-delete-gamesid-security-enforcement).
- [ADR-002](adr/ADR-002.md) and [ADR-003](adr/ADR-003.md) explain the containerized deployment and gateway setup that support a reliable runtime environment and the public interface described in [QR-003](../quality-requirements.md#qr-003-public-author-api-contract).
- [ADR-004](adr/ADR-004.md) and [ADR-005](adr/ADR-005.md) document the database migration strategy and the layered backend structure that make the implementation of these quality requirements easier to maintain and evolve.

In other words, the component, sequence, and deployment diagrams describe how the system is built and operated, while the ADRs explain why those choices were made and how they relate to the documented quality requirements.

---

## Dynamic View — Sequence Diagram

### Diagram Source

The sequence diagram is maintained as a PlantUML file:

[`dynamic-view/create-game-sequence.puml`](dynamic-view/create-game-sequence.puml)

![Create game sequence](./dynamic-view/create-game-sequence.svg)

### What the Diagram Shows

The sequence diagram illustrates the **"Create Game" workflow**, which is a non-trivial request involving multiple components and interactions:

1. **User navigates to Create Game page**: The user clicks "Create Game" in the browser. The `ProtectedRoute` component checks the `AuthContext` for a valid JWT token. If no token exists, the user is redirected to the authentication page.

2. **User fills in game details**: The user enters a title, description, banner URL, and tags, then clicks "Submit". The frontend validates the form fields.

3. **Frontend sends authenticated POST request**: The browser sends a `POST /api/games` request with the game data and an `Authorization: Bearer <JWT>` header. Nginx reverse-proxies this to the backend at `http://backend:8080/games`.

4. **Backend validates JWT token**: The `JwtFilter` intercepts the request, validates the JWT token using `JwtUtils`, and sets the authentication principal (user ID) on the request.

5. **Backend processes game creation**: The `GamesController` checks authentication (returns 401 if not authenticated), then delegates to `GamesService.createGame()`.

6. **Service layer creates game entity**: `GamesService` creates a `GamesEntity` and saves it via `GamesRepository` (JPA/Hibernate) to PostgreSQL. The database generates an ID and timestamps.

7. **Service handles tags**: The service resolves tag names to `TagEntity` references and creates `GameTagEntity` join records in the database.

8. **Response flows back**: The created game entity is returned through the service → controller → Nginx → browser chain with HTTP 201 Created status.

9. **Frontend updates UI**: The browser updates its state with the new game data and shows a success message or redirects to the game page.

### Why This Scenario Is Important

The "Create Game" workflow is the **core value-creating operation** of the GDE Website. It demonstrates:

- **Authentication and authorization**: Only authenticated users can create games. The JWT token is validated at every request.
- **Data integrity**: The game is created with proper timestamps, author association, and tag relationships.
- **Multi-component interaction**: The flow involves the browser, Nginx, Spring Security, the controller, the service layer, the repository, and the database.
- **Error handling**: The flow includes authentication checks (401 UNAUTHORIZED) and form validation.

### Architecture Decisions and Quality Requirements

This sequence diagram helps the reader reason about:

- **Stateless JWT authentication**: The diagram shows how the JWT token flows from the browser through Nginx to the backend, where it is validated by the JwtFilter. This is a key architectural decision that affects security, scalability, and session management.
- **Nginx reverse proxy**: The diagram shows how Nginx acts as a gateway between the frontend and backend, providing a single entry point and enabling CORS handling.
- **Service layer pattern**: The diagram shows the Controller → Service → Repository chain, which is a standard pattern for separating concerns and enabling testability.
- **Database interactions**: The diagram shows how JPA/Hibernate interacts with PostgreSQL, including the creation of game entities and tag relationships.
- **Quality requirements**: The diagram directly relates to QR-001 (PATCH /games/{id} security enforcement) and QR-002 (DELETE /games/{id} security enforcement), as the same authentication pattern is used for all write operations.

---

## Deployment View — Deployment Diagram

### Diagram Source

The deployment diagram is maintained as a PlantUML file:

[`deployment-view/deployment-diagram.puml`](deployment-view/deployment-diagram.puml)

![Deployment Diagram](./deployment-view/deployment-diagram.svg)

### What the Diagram Shows

The deployment diagram illustrates the **runtime and deployment structure** of the GDE Website, including:

1. **Customer-facing access path**: The user accesses the application through a web browser over HTTP (port 80). The request reaches the Nginx container, which serves the React SPA and reverse-proxies API requests to the backend.

2. **Main deployed services**:
   - **frontend** (Nginx container): Serves the built React SPA and reverse-proxies `/api/` requests to the backend. Exposed on port 80.
   - **backend** (Spring Boot container): Runs the Spring Boot application with Java 21 JRE. Exposed on port 8080.
   - **postgres** (PostgreSQL container): The sole data store. Uses a persistent Docker volume (`postgres_data`).
   - **pgAdmin** (pgAdmin4 container): Database administration UI. Exposed on port 5050.

3. **Observability services** (optional overlay):
   - **grafana** (Grafana container): Dashboard UI. Exposed on port 3000.
   - **prometheus** (Prometheus container): Metrics storage. Exposed on port 9090.
   - **loki** (Loki container): Log aggregation. Exposed on port 3100.
   - **alloy** (Alloy container): Log collection from Docker containers. Exposed on port 12345.
   - **cadvisor** (cAdvisor container): Container resource metrics. Exposed on port 8081.
   - **docker-events** (Docker CLI container): Streams Docker lifecycle events.

4. **Datastores and stateful infrastructure**:
   - `postgres_data` volume (PostgreSQL data)
   - `prometheus_data` volume (Prometheus TSDB)
   - `loki_data` volume (Loki log storage)
   - `grafana_data` volume (Grafana state)
   - `alloy_data` volume (Alloy state)

5. **Network boundaries**: All services share a single Docker bridge network (`gde-network`). The observability overlay uses an external network reference (`swp_team30_gde-network`).

6. **Compose files**: The deployment is defined in two Docker Compose files:
   - `compose.yaml`: Main application stack (frontend, backend, postgres, pgAdmin)
   - `compose.observability.yaml`: Observability overlay (Grafana, Prometheus, Loki, Alloy, cAdvisor, docker-events)

### Why the Deployment Model Was Chosen

The deployment model was chosen for the following reasons:

- **Simplicity**: Docker Compose provides a simple, declarative way to define and run multi-container applications. It is easy to set up and understand for a small team.
- **Local development**: The Docker Compose setup is designed for local development and testing. It allows developers to run the entire stack with a single command (`docker compose up`).
- **Portability**: Docker containers are portable across environments (Linux, Windows, macOS). The same Docker Compose files can be used for development, testing, and production.
- **Separation of concerns**: The main application stack and the observability overlay are separated into two Compose files, allowing the observability stack to be started and stopped independently.
- **Cost efficiency**: The deployment model does not require any cloud infrastructure or orchestration tools (Kubernetes, ECS, etc.). It runs on a single Docker host, which can be a local machine or a VM.

### How the Current Deployment Supports or Constrains the Product

**Supports:**

- **Rapid development**: The Docker Compose setup allows developers to quickly spin up the entire stack and iterate on features.
- **Consistent environments**: All developers run the same Docker images and configurations, reducing "it works on my machine" issues.
- **Observability**: The optional observability overlay provides centralized logging and metrics, which is valuable for debugging and monitoring.
- **Scalability potential**: The stateless backend and containerized deployment make it possible to scale horizontally by adding more backend instances behind a load balancer.

**Constrains:**

- **Single host**: The current deployment model runs all services on a single Docker host. This limits scalability and availability.
- **No CI/CD deployment pipeline**: The deployment is manual (run `docker compose up`). There is no automated deployment pipeline for production.
- **No load balancing**: There is no load balancer or reverse proxy in front of the backend. The Nginx container serves as a reverse proxy for the frontend, but it does not load-balance across multiple backend instances.
- **No database replication**: PostgreSQL runs as a single instance with no replication or failover.

### What Must Be Considered When Deploying or Operating

1. **Environment variables**: The `.env.secret` file must be created from `.env.example` with proper values for JWT_SECRET, POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD, PGADMIN_DEFAULT_EMAIL, PGADMIN_DEFAULT_PASSWORD, and Grafana credentials.
2. **Network configuration**: The `gde-network` bridge network must be created before starting the observability overlay. The observability Compose file references it as an external network.
3. **Volume persistence**: The `postgres_data` volume must be backed up regularly. The observability volumes (prometheus_data, loki_data, grafana_data, alloy_data) are less critical but should be considered for data retention.
4. **Health checks**: All services have health checks defined in the Compose files. The backend depends on the postgres health check, and the frontend depends on the backend.
5. **Port conflicts**: The exposed ports (80, 8080, 3000, 3100, 5050, 8081, 9090, 12345) must not conflict with other services on the host.
6. **Security**: The default Grafana credentials (admin/admin) should be changed for non-local deployments. The JWT_SECRET should be a strong, random value.
7. **Database migrations**: Flyway runs automatically on backend startup. Ensure that migrations are backward-compatible and tested before deployment.
8. **Observability**: The observability stack is optional and can be started/stopped independently. It is designed for local development and debugging.

---

### Diagram Files

| Diagram | Source File | Description |
|---------|------------|-------------|
| Component Diagram | [`static-view/component-diagram.puml`](static-view/component-diagram.puml) | Static view of system components and their relationships |
| Sequence Diagram | [`dynamic-view/create-game-sequence.puml`](dynamic-view/create-game-sequence.puml) | Dynamic view of the "Create Game" workflow |
| Deployment Diagram | [`deployment-view/deployment-diagram.puml`](deployment-view/deployment-diagram.puml) | Deployment structure and runtime topology |

---

## Quality Attributes

The architecture supports the following quality attributes (based on ISO/IEC 25010):

| Quality Attribute | Sub-characteristic | How It Is Addressed |
|-------------------|-------------------|---------------------|
| **Security** | Authenticity | JWT authentication with BCrypt password hashing. Stateless sessions. Explicit endpoint security rules. |
| **Security** | Confidentiality | Environment variables for secrets (`.env.secret`). No secrets in source code. |
| **Functional Suitability** | Functional completeness | Core features: game listing, game creation, game editing, user registration, tag filtering. |
| **Functional Suitability** | Functional correctness | Input validation, authentication checks, error handling. |
| **Maintainability** | Modularity | Layered architecture (Controller, Service, Repository). Module-based packages (Games, Users, Forum, Store). |
| **Maintainability** | Reusability | Shared frontend components (NavBar, Layout, ProtectedRoute). Reusable API client. |
| **Maintainability** | Analyzability | Diagrams-as-code approach. Comprehensive documentation. Observability stack for debugging. |
| **Performance** | Time behavior | Nginx serves static files efficiently. Backend uses JPA with pagination. |
| **Reliability** | Availability | Docker health checks. Container restart policies (`unless-stopped`). |
| **Portability** | Adaptability | Docker containers are portable across environments. Docker Compose for local development. |

---
