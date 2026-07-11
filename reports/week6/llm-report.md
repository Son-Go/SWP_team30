# LLM Usage Report — Week 6 / Sprint 4

## Tools Used

- ChatGPT / Google Gemini / Qwen — used for technical clarification, Spring Boot and Spring Security implementation guidance, API design review, and documentation drafting.
- Codex / other coding agents — used to review code structure, suggest refactoring approaches, generate JavaDoc drafts, and prepare verification scenarios.
- Postman — used by the team for manual API verification; it is not an LLM tool, but it was used to validate the implementation after LLM-assisted planning.
- GitHub Copilot or IDE assistants — used where available for code completion, navigation, and small boilerplate suggestions.

## Usage Details

### ChatGPT / Google Gemini / Qwen

- LLMs were used to review the idea of a shared `checkNotBanned` validation method and identify where it should be applied: game creation, game editing, game deletion, comment creation, comment editing, and comment deletion.
- LLMs helped explain Spring Security concepts, including the difference between authentication and authorization, expected `403 Forbidden` responses for banned users, JWT-based user identification, and administrator permission checks.
- LLMs assisted with drafting and reviewing customer-facing documentation for Assignment 6, including `README.md`, `docs/customer-handover.md`, `CONTRIBUTING.md`, `AGENTS.md`, roadmap updates, UAT scenarios, retrospective, and reflection documents.

### Codex / Other Coding Agents

- Coding agents were used to suggest JavaDoc documentation for new or modified DTOs, services, repository methods, and authorization-related code.
- Agents were used as a review aid for service-layer changes, especially to identify duplicated validation calls and potential missing calls after refactoring.
- Agents suggested a structure for separating authorization checks into reusable methods instead of repeating role and banned-status checks in every controller endpoint.
- Generated code and documentation were reviewed and adapted by team members before being added to the repository. The team remained responsible for correctness, security decisions, testing, and final commits.

### Postman

- The team manually verified API behavior after implementation.
- Verification included normal-user flows and restricted-user flows: creating games, editing games, deleting games, creating comments, editing comments, deleting comments, banning users, and unbanning users.
- The team checked that a banned user receives `403 Forbidden` when attempting protected content actions and that existing comments are removed when the user is banned.

## Human Review and Validation

- Team members reviewed all generated suggestions before applying them.
- No LLM was given production credentials, private keys, `.env.secret` contents, database passwords, JWT secrets, or other confidential deployment data.
- The team tested the final behavior manually through Postman and reviewed relevant application logs.
- LLM output was treated as implementation support and documentation assistance, not as an authoritative source or an automatic code change.
