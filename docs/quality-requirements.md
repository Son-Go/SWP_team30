# Quality requirements

## MVP v2 scope and evidence

The current quality requirements remain aligned with the MVP v2 and Sprint 3 scope: secure game administration, a stable public author contract, and deployment behavior that matches the documented architecture. The evidence for these requirements is maintained in [testing.md](testing.md), [quality-requirement-tests.md](quality-requirement-tests.md), and the CI workflow for frontend, backend, and end-to-end verification.

The relevant automated evidence covers:

- protected game update/delete operations through backend HTTP integration tests
- public author API access through the dedicated HTTP integration test
- deployment and proxy integrity through the additional QA checks and Docker Compose smoke/e2e execution
- traceability to the accepted ADRs for authentication, deployment, reverse proxying, persistence, and layered architecture

## QR-001: PATCH /games/{id} security enforcement

**ISO/IEC 25010 sub-characteristic:** Security / Authenticity

**Scenario:** When an unauthenticated client attempts PATCH /games/{id}, the API shall reject the request and require a valid JWT token.

**Why this matters:** Protecting game updates prevents unauthorized changes and preserves data integrity and trust in the application.

**Automated evidence:** [QRT-001](quality-requirement-tests.md#qrt-001-patch-gamesid-security-enforcement) and the corresponding HTTP integration test in [EndpointHttpIntegrationTest.java](../backend/gde_website/src/test/java/gde/gde_website/EndpointHttpIntegrationTest.java).

**Related ADRs:** [ADR-001](architecture/adr/ADR-001.md), [ADR-003](architecture/adr/ADR-003.md), [ADR-005](architecture/adr/ADR-005.md)


## QR-002: DELETE /games/{id} security enforcement

**ISO/IEC 25010 sub-characteristic:** Security / Authenticity

**Scenario:** When an unauthenticated client attempts DELETE /games/{id}, the API shall reject the request and require a valid JWT token.

**Why this matters:** Enforcing authentication for deletions prevents malicious or accidental data loss and preserves user data safety.

**Automated evidence:** [QRT-002](quality-requirement-tests.md#qrt-002-delete-gamesid-security-enforcement) and the corresponding HTTP integration test in [EndpointHttpIntegrationTest.java](../backend/gde_website/src/test/java/gde/gde_website/EndpointHttpIntegrationTest.java).

**Related ADRs:** [ADR-001](architecture/adr/ADR-001.md), [ADR-003](architecture/adr/ADR-003.md), [ADR-005](architecture/adr/ADR-005.md)


## QR-003: Public author API contract

**ISO/IEC 25010 sub-characteristic:** Functional suitability

**Scenario:** When any client invokes GET /games/author/{id}, the API shall remain publicly reachable and return the expected author JSON contract.

**Why this matters:** A stable public author endpoint ensures external consumers and UI components can retrieve author data without authentication failures.

**Automated evidence:** [QRT-003](quality-requirement-tests.md#qrt-003-public-author-endpoint-contract) and the corresponding HTTP integration test in [EndpointHttpIntegrationTest.java](../backend/gde_website/src/test/java/gde/gde_website/EndpointHttpIntegrationTest.java).

**Related ADRs:** [ADR-002](architecture/adr/ADR-002.md), [ADR-003](architecture/adr/ADR-003.md), [ADR-004](architecture/adr/ADR-004.md), [ADR-005](architecture/adr/ADR-005.md)