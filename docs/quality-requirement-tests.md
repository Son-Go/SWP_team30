# Quality requirement tests

These checks are the explicit automated evidence for the quality requirements that matter to the current MVP v2 scope. They run as part of the backend Maven verification in CI and are therefore preserved alongside the other workflow artifacts for review.

## QRT-001 PATCH /games/{id} security enforcement

Ensures the `updateGameRequiresJwtOverHttp` test rejects unauthenticated PATCH /games/{id} requests.

Test location: [updateGameRequiresJwtOverHttp()](../backend/gde_website/src/test/java/gde/gde_website/EndpointHttpIntegrationTest.java)

This requirement remains relevant for the sprint because authenticated content updates are part of the main game management flow and should stay protected at the HTTP layer.


## QRT-002 DELETE /games/{id} security enforcement

Ensures the `deleteGameRequiresJwtOverHttp` test rejects unauthenticated DELETE /games/{id} requests.

Test location: [deleteGameRequiresJwtOverHttp()](../backend/gde_website/src/test/java/gde/gde_website/EndpointHttpIntegrationTest.java)

This requirement protects the destructive write path and complements the update-security check for the same product area.

## QRT-003 Public author endpoint contract

Ensures the `authorEndpointReturnsPublicAuthorContractOverHttp` test keeps GET /games/author/{id} publicly reachable and returns the expected author JSON contract.

Test location: [authorEndpointReturnsPublicAuthorContractOverHttp()](../backend/gde_website/src/test/java/gde/gde_website/EndpointHttpIntegrationTest.java)

This requirement protects the public-facing author information flow that the UI depends on and helps guard the API contract from accidental drift.