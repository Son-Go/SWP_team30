## QR-001: PATCH /games/{id} security enforcement

**ISO/IEC 25010 sub-characteristic:** Security / Authenticity

**Scenario:** When an unauthenticated client attempts PATCH /games/{id}, the API shall reject the request and require a valid JWT token.

**Why this matters:** Protecting game updates prevents unauthorized changes and preserves data integrity and trust in the application.

**Related ADRs:** [ADR-001](architecture/adr/ADR-001.md), [ADR-003](architecture/adr/ADR-003.md), [ADR-005](architecture/adr/ADR-005.md)

**Linked quality requirement tests:** [QRT-001](quality-requirement-tests.md#qrt-001-patch-gamesid-security-enforcement)


## QR-002: DELETE /games/{id} security enforcement

**ISO/IEC 25010 sub-characteristic:** Security / Authenticity

**Scenario:** When an unauthenticated client attempts DELETE /games/{id}, the API shall reject the request and require a valid JWT token.

**Why this matters:** Enforcing authentication for deletions prevents malicious or accidental data loss and preserves user data safety.

**Related ADRs:** [ADR-001](architecture/adr/ADR-001.md), [ADR-003](architecture/adr/ADR-003.md), [ADR-005](architecture/adr/ADR-005.md)

**Linked quality requirement tests:** [QRT-002](quality-requirement-tests.md#qrt-002-delete-gamesid-security-enforcement)


## QR-003: Public author API contract

**ISO/IEC 25010 sub-characteristic:** Functional suitability

**Scenario:** When any client invokes GET /games/author/{id}, the API shall remain publicly reachable and return the expected author JSON contract.

**Why this matters:** A stable public author endpoint ensures external consumers and UI components can retrieve author data without authentication failures.

**Related ADRs:** [ADR-002](architecture/adr/ADR-002.md), [ADR-003](architecture/adr/ADR-003.md), [ADR-004](architecture/adr/ADR-004.md), [ADR-005](architecture/adr/ADR-005.md)

**Linked quality requirement tests:** [QRT-003](quality-requirement-tests.md#qrt-003-public-author-endpoint-contract)