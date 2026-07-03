# SWP_team30 GDE website

## Description
GDE Website - website for GDE innopolis club, where local game developers can publish their indie games and russian gamer can explore new games

---

## Access instructions

To access deployed MVP-v0 open [link](https://precious-toffee-52e805.netlify.app/)

To access deployed MVP-v1 open [link](http://gde.maxmir.ru/)

---

## Local setup instructions

**MVP-v0:** Open https://github.com/Son-Go/SWP_team30/tree/8c2e78c353f0c7f9470b3c63cf59da54a8ecf47e/temp-frontend/htmlFrontend folder, and run VS code Live server extension. MVP-v0 has no backend functionality

**MVP-v1:** Clone project, copy [.env.example](/.env.example) to `.env.secret`, run docker compose up. `http://localhost:80` will show working website

---

## Weekly README reports

- Week 2 [README.md](https://github.com/Son-Go/SWP_team30/blob/8b765b5d98da638555c6fb2a9f5be49d44b7cc38/reports/week2/README.md)
- Week 3 [README.md](https://github.com/Son-Go/SWP_team30/blob/1214b680442a30e2ae7adfb5d748f33b7229d92b/reports/week3/README.md)
- Week 4 [README.md](https://github.com/Son-Go/SWP_team30/blob/main/reports/week4/README.md)

---

## Testing

All testing info can be found in:

- [testing.md](/docs/testing.md) - all automated tests
- [endpoint-testing.md](/docs/endpoint-testing.md) - manual endpoint testing
- [user-acceptance-tests.md](/docs/user-acceptance-tests.md) - UAT tests
- [quality-requirements.md](/docs/quality-requirements.md) - qualiy requirements with links to automated tests

--- 

## Logging

Test contain several logging tools to log container health and system stability

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


## Visuals

---

## Project status
**Active development**

**Version:** v0.1.5 (MVP-v1 status)

[CHANGELOG](./CHANGELOG.md)

## Documentation

* [Development Process & Configuration Management](docs/development-process.md)

---

## License
[LICENCE](https://github.com/Son-Go/SWP_team30/blob/8b765b5d98da638555c6fb2a9f5be49d44b7cc38/LICENSE)

---