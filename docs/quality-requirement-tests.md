## QRT-001 PATCH /games/{id} security enforcement

Ensures the `updateGameRequiresJwtOverHttp` test rejects unauthenticated PATCH /games/{id} requests.


## QRT-002 DELETE /games/{id} security enforcement

Ensures the `deleteGameRequiresJwtOverHttp` test rejects unauthenticated DELETE /games/{id} requests.


## QRT-003 Public author endpoint contract

Ensures the `authorEndpointReturnsPublicAuthorContractOverHttp` test keeps GET /games/author/{id} publicly reachable and returns the expected author JSON contract.