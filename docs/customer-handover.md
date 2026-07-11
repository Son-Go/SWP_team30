# Customer Handover Guide

**Product:** GDE Website  
**Repository:** [Son-Go/SWP_team30](https://github.com/Son-Go/SWP_team30)  
**Current version:** v0.2.0 (MVP-v2)  
**Handover status:** Assignment 6 — product and documentation are available for review; infrastructure ownership transfer is not yet recorded as complete.

---

## Purpose

GDE Website is a platform for the GDE Innopolis club where indie game developers can publish and manage games, while community users can browse and discover them.

This guide describes the current operational state of the product, what a customer or reviewer can access now, and what remains under the team’s control.

---

## Current Access

| Item | Current location or access point | Current handover state |
|---|---|---|
| Source repository | [GitHub repository](https://github.com/Son-Go/SWP_team30) | Available for review |
| Production application | [http://gde.maxmir.ru/](http://gde.maxmir.ru/) | Available for customer use and review |
| Hosted documentation | [https://son-go.github.io/SWP_team30/](https://son-go.github.io/SWP_team30/) | Available for customer and TA review |
| Local deployment configuration | `compose.yaml` and `.env.example` | Available in repository |
| Observability configuration | `compose.observability.yaml` | Available in repository; optional |
| Production VM access | Managed by the project team | Not transferred or delegated as part of the documented Assignment 6 state |
| Domain and DNS ownership | Managed by the project team | Not transferred or delegated as part of the documented Assignment 6 state |
| Production credentials and secrets | Kept outside the repository | Not exposed or transferred through GitHub |

---

## Transition Scope

At the current handover level, the customer receives access to:

- The public product URL.
- The public repository and maintained source code.
- The hosted documentation site.
- Local setup and deployment configuration templates.
- Architecture, API, testing, quality, and troubleshooting documentation.

The following remain intentionally retained by the project team until a secure ownership-transfer process is agreed and completed:

- SSH access to the production VM.
- Production database credentials.
- Production environment files and secret values.
- Domain registrar and DNS administration access.
- Any third-party service accounts used by the deployment.

This separation prevents credentials and infrastructure access from being exposed in the public repository.

---

## Customer Entry Points

Use the following documentation depending on the task:

| Need | Documentation |
|---|---|
| Start using or reviewing the product | [README.md](../README.md) |
| Access the current deployed application | [http://gde.maxmir.ru/](http://gde.maxmir.ru/) |
| Browse maintained documentation | [Hosted documentation site](https://son-go.github.io/SWP_team30/) |
| Understand the architecture | [Architecture documentation](architecture/README.md) |
| Review REST API endpoints | [Endpoint overview](endpoint-overview.md) |
| Run or review tests | [Testing guide](testing.md) |
| Review quality requirements | [Quality requirements](quality-requirements.md) |
| Diagnose operational issues | [Observability guide](observability.md) |
| Contribute changes | [CONTRIBUTING.md](../CONTRIBUTING.md) |

---

## Local Setup

### Prerequisites

- Git
- Docker Engine
- Docker Compose

### Start the application

```bash
git clone https://github.com/Son-Go/SWP_team30.git
cd SWP_team30

cp .env.example .env.secret
```

Fill in the required local values in `.env.secret`. Do not commit this file.

```bash
docker compose up --build
```

After the containers start, open:

```text
http://localhost:80
```

### Stop the application

```bash
docker compose down
```

### Reset local database data

> Warning: this removes local Docker volumes and local database data.

```bash
docker compose down -v
docker compose up --build
```

---

## Configuration and Secrets

The repository includes `.env.example` as the non-secret configuration template.

The customer or future maintainer must know the following rules:

- Copy `.env.example` to `.env.secret` for local execution.
- Store actual passwords, tokens, database credentials, and server configuration only in `.env.secret` or a secure secrets-management location.
- Never commit `.env.secret`, production credentials, private keys, JWT secrets, or database dumps.
- When a new environment variable is introduced, update `.env.example` and the relevant setup documentation without adding real secret values.
- Production environment variables must be transferred only through a secure channel if production ownership is formally transferred.

---

## Deployment and Operations

The project is packaged as Docker containers and is started through Docker Compose.

```bash
docker compose up --build
```

The optional observability stack can be started with:

```bash
docker compose -f compose.yaml -f compose.observability.yaml up
```

When enabled locally, Grafana is available at:

```text
http://localhost:3000
```

The production deployment currently remains operated by the project team. A customer can use the production website, but cannot independently redeploy it until the team transfers VM, DNS, deployment, and secret-management access.

For database backup and recovery guidance, see [Database backup documentation](db-backup-vm.md).

---

## Verification Steps

A customer, TA, or future maintainer can verify the current product state using these steps:

1. Open [http://gde.maxmir.ru/](http://gde.maxmir.ru/) and confirm that the application loads.
2. Register a test account or log in with an existing account.
3. Browse the game catalogue.
4. Filter games by tags.
5. Create or edit a game when using an account with the required permissions.
6. Run the local stack with `docker compose up --build`.
7. Review automated and manual checks in [testing.md](testing.md).
8. Compare available functionality with [user acceptance tests](user-acceptance-tests.md).

---

## Recovery Guidance

| Situation | Action |
|---|---|
| Local application does not start | Run `docker compose ps` and inspect logs with `docker compose logs` |
| Backend fails to connect to database | Check values in `.env.secret`; confirm containers are running |
| Frontend does not reflect recent changes | Run `docker compose build frontend` and restart the stack |
| Local database is corrupted or disposable | Run `docker compose down -v`, then `docker compose up --build` |
| Production site is unavailable | Contact the project team because production VM access remains retained |
| Metrics or logs are unavailable locally | Start the observability overlay and consult [observability.md](observability.md) |

---

## Known Limitations

- Game cover images are stored as the URL links in database; external object storage is not configured.
- The observability stack is optional and is not part of the default Compose startup.
- Account registration does not include email verification.
- There is no dedicated customer-facing admin panel for content moderation.
- MVP-v0 in `temp-frontend/` is a static prototype and is not connected to the backend.
- Production infrastructure ownership has not yet been transferred to the customer.

---

## Documentation Sufficiency

The current documentation set is sufficient for:

- Product review and normal user access.
- Local setup and development.
- Reviewing architecture, API behaviour, tests, and quality requirements.
- Basic local troubleshooting.
- Understanding which infrastructure access remains with the team.

The following support is still necessary for a full operational ownership handover:

- Secure transfer of production VM access.
- Secure transfer or rotation of production secrets.
- Transfer of domain registrar and DNS administration access.
- Confirmation of production database backup and recovery responsibility.
- A final operational walkthrough with the future product owner.

Until these actions are completed and recorded, the project team remains responsible for production infrastructure operation.

---

## Handover Completion Checklist

Complete this checklist only when each transfer has actually happened.

- [ ] Customer has confirmed access to the source repository.
- [ ] Customer has confirmed access to the hosted documentation.
- [ ] Customer has confirmed access to the production application.
- [ ] Production VM access has been securely transferred or delegated.
- [ ] Production environment variables and secrets have been securely transferred or rotated.
- [ ] Domain and DNS ownership has been transferred or delegated.
- [ ] Database backup and recovery responsibility has been agreed.
- [ ] Customer has completed a deployment or recovery walkthrough.
- [ ] Customer has accepted the final handover state.

---

## Support

Before full infrastructure ownership transfer, contact the project team for:

- Production incidents.
- Deployment requests.
- Domain or DNS changes.
- Production credential rotation.
- Database restoration.
- Questions not resolved by the documentation listed above.
