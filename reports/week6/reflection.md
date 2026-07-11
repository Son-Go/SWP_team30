# Reflection on Sprint-4

## Learning points

- **Authorization testing:** Role-based functionality must be tested as complete user flows, not only as separate endpoints. A change in ban or permission logic affects game creation, editing, deletion, comments, and administrator actions.
- **Refactoring safety:** After refactoring a service, the team must verify that important validation calls, such as checks preventing banned users from performing actions, were not accidentally removed.
- **Documentation maintenance:** Customer-facing documentation should be updated together with the feature. When access, workflows, API behaviour, testing scope, or deployment information changes, the README, UATs, roadmap, and handover documentation must remain consistent.

## Validated assumptions

- **Confirmed:** Administrators need moderation functionality to ban and unban users when inappropriate activity occurs.
- **Confirmed:** Banned users must not be able to create, edit, or delete games and comments.
- **Confirmed:** Comments are an important interaction feature for users reviewing and discussing games.
- **Confirmed:** The project needs clear customer-facing handover documentation before the final review and transition stage.
- **Rejected:** It was not sufficient to protect only game-related actions; comment creation, editing, and deletion also required the same banned-user restrictions.

## Friction and gaps

- Some authorization checks had to be reviewed after refactoring because a missing validation call could allow a banned user to create content.
- Testing moderation functionality required multiple accounts, roles, JWT tokens, and sequential Postman requests, which made manual verification more time-consuming.
- Not all Sprint 4 work initially had dedicated GitHub issues, making traceability between implementation, roadmap tasks, UAT evidence, and Pull Requests less clear.
- Several maintained documents required updates at once, which created a risk that repository documentation could describe an outdated implementation or handover state.

## Planned response

- Create and maintain a Postman collection for normal-user, administrator, and banned-user scenarios, including positive and negative checks for games and comments.
- Add regression tests for banned-user restrictions whenever roles, authentication, or content-management services are changed.
- Create or link GitHub issues for every Sprint 4 and Sprint 5 deliverable, then connect them to milestones, Pull Requests, UAT scenarios, and release notes.
- Use a documentation checklist before merging changes that affect APIs, permissions, product access, environment configuration, deployment, testing, or customer handover information.
- Complete Assignment 6 documentation deliverables: update `README.md`, maintain `docs/customer-handover.md`, and add or update `CONTRIBUTING.md` and `AGENTS.md`.
