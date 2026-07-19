# Customer Handover Guide

**Product:** GDE Website  
**Repository:** [Son-Go/SWP_team30](https://github.com/Son-Go/SWP_team30)  
**Current version:** v0.3.0 (MVP-v3)  
**Handover status:** Ready for independent use, Accepted with follow-up items

Right now customer is not yet decided, when he will deploy and start use product, so it not yet used by the customer. 

Also customer approved user-facing documentation on prevoius week, hovewer we were unable to conduct meeting or get confirmation from the customer on week 7 due to internet shortage in the area, where customer was located this week. So right now we are in the semi-transitioned state because we were unable to contact to the customer
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
| Production VM access | Managed by the project team | No needed. Customer will use his own VM |
| Domain and DNS ownership | Managed by the customer | During development no Domains were bought, because customer want to do it by himseslf |
| Production credentials and secrets | Kept outside the repository | Customer primary admin account was added to db init script, so it hardcoded to every new instance of DB |

---

## Transition Scope

At the current handover level, the customer receives access to:

- The public product URL.
- The public repository and maintained source code.
- The hosted documentation site.
- Local setup and deployment configuration templates.
- Production database credentials.
- Production environment files and secret values.
- Architecture, API, testing, quality, and troubleshooting documentation.

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
- VM or any machine with at least 2 core CPU 2.4+ GHz, 4 GB RAM and 20+ GB disk space

### Start the application

```bash
git clone https://github.com/Son-Go/SWP_team30.git
cd SWP_team30

cp .env.example .env.secret
```

Fill in the required local values in `.env.secret`. Do not commit this file.

```bash
docker compose up -d --build
```

After the containers start, use dedicated script to fill database with required game tags and Primary admin account:

```bash
./scripts/fill-db.ps1
```

Open project on dedicated port:

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
| Local database is corrupted or disposable | Open [db-backup-vm.md](db-backup-vm.md) for db recovery instructions |
| Production site is unavailable | Restart project or check the logs in observability stack |
| Metrics or logs are unavailable locally | Start the observability overlay and consult [observability.md](observability.md) |

---

## Known Limitations

- Game cover images are stored as the URL links in database; external object storage is not configured.
- The observability stack is optional and is not part of the default Compose startup.
- Account registration does not include email verification.
- MVP-v0 in `temp-frontend/` is a static prototype and is not connected to the backend.

---

## Documentation Sufficiency

The current documentation set is sufficient for:

- Product review and normal user access.
- Local setup and development.
- Reviewing architecture, API behaviour, tests, and quality requirements.
- Basic local troubleshooting.
- Understanding which infrastructure access remains with the team.

---

## Handover Completion Checklist

Complete this checklist only when each transfer has actually happened.

- [x] Customer has confirmed access to the source repository.
- [x] Customer has confirmed access to the hosted documentation.
- [x] Customer has confirmed access to the production application.
- [ ] Customer obtained VM for website (customer said, he will deploy project sometime later, right now it stays as instance on development VM)
- [x] Production environment variables and secrets have been securely transferred or rotated.
- [ ] Customer obtained domain for website (same as for VM)
- [x] Database backup and recovery responsibility has been agreed.
- [ ] Customer has completed a deployment or recovery walkthrough. (same as for VM)
- [x] Customer has accepted the final handover state.

---

## Support

Before full infrastructure ownership transfer, contact the project team for:

- Production incidents.
- Deployment requests.
- Production credential rotation.
- Database restoration.
- Questions not resolved by the documentation listed above.
