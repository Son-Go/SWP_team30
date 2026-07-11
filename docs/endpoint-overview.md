# API Endpoint Overview

Base URL:
- Local development: http://localhost:8080

## Authentication
- The API uses JWT-based authentication.
- For protected endpoints, send:
  - Header: `Authorization: Bearer <token>`
- Tokens are returned by the login and register endpoints.

## Content type
- JSON endpoints should send:
  - Header: `Content-Type: application/json`

## Public endpoints

### 1. Get all games
- Method: `GET`
- Path: `/games`
- Purpose: Retrieve a paginated list of games, optionally filtered by tags.
- Auth required: No
- Query parameters:
  - `page` (optional, default `0`)
  - `size` (optional, default `24`)
  - `tags` (optional, repeatable, e.g. `?tags=RPG&tags=Adventure`)
- Notes:
  - If `tags` are provided, the endpoint returns games that match at least one of the provided tags.
  - Results are ordered by `createdAt` descending.
  - The endpoint returns both approved and non-approved games; `isApproved` is exposed in each item but is not used as a filter.
- Success response: `200 OK`
- Response body: a paginated Spring Data `Page` object containing an array of games under `content`. Each item has the shape of `GamesPageResponse` with fields `id`, `authorId`, `title`, `shortDescription`, `description`, `bannerUrl`, `createdAt`, `author`, `isApproved`, `gameTags`, and `pictures`.

Example response:
```json
{
  "content": [
    {
      "id": 1,
      "authorId": 2,
      "title": "Example Game",
      "shortDescription": "A short card description",
      "description": "A sample game",
      "bannerUrl": "https://example.com/banner.png",
      "createdAt": "2026-01-01T12:00:00Z",
      "author": {
        "username": "alice",
        "profile_image_url": null,
        "email": "alice@example.com"
      },
      "isApproved": true,
      "gameTags": {
        "GENRE": ["RPG", "Adventure"],
        "MODE": []
      },
      "pictures": [
        "https://example.com/screenshot1.png",
        "https://example.com/screenshot2.png"
      ]
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 24,
  "number": 0,
  "first": true,
  "last": true,
  "empty": false
}
```

---

### 2. Get one game by ID
- Method: `GET`
- Path: `/games/{id}`
- Purpose: Retrieve a single game by its ID.
- Auth required: No
- Success response: `200 OK`
- Response body: detailed game object

Example response:
```json
{
  "id": 1,
  "authorId": 2,
  "title": "Example Game",
  "description": "A sample game",
  "bannerUrl": "https://example.com/banner.png",
  "createdAt": "2026-01-01T12:00:00Z",
  "updatedAt": "2026-01-01T12:00:00Z",
  "isOwner": false,
  "isApproved": true,
  "author": {
    "username": "alice",
    "profile_image_url": null,
    "email": "alice@example.com"
  },
  "gameTags": {
    "GENRE": ["RPG"]
  },
  "screenshots": {
    "videos": [],
    "pictures": []
  }
}
```

---

### 3. Get game author by ID
- Method: `GET`
- Path: `/games/author/{id}`
- Purpose: Retrieve public information about the author of a game.
- Auth required: No
- Success response: `200 OK`

Example response:
```json
{
  "username": "alice",
  "profile_image_url": null,
  "email": "alice@example.com"
}
```

---

### 4. Get all available tags
- Method: `GET`
- Path: `/games/tags/all`
- Purpose: Retrieve all tags grouped by tag type.
- Auth required: No
- Success response: `200 OK`

Example response:
```json
{
  "gameTags": {
    "GENRE": ["RPG", "Adventure", "Strategy"]
  }
}
```

---

### 5. Login
- Method: `POST`
- Path: `/auth/login`
- Purpose: Authenticate a user and receive a JWT token.
- Auth required: No
- Body:
```json
{
  "authInfo": "user@example.com",
  "isEmail": true,
  "password": "your-password"
}
```

Success response: `200 OK`

Example response:
```json
{
  "token": "jwt-token-here"
}
```

---

### 6. Register
- Method: `POST`
- Path: `/auth/register`
- Purpose: Create a new account and receive a JWT token.
- Auth required: No
- Body:
```json
{
  "username": "alice",
  "email": "alice@example.com",
  "password": "your-password",
  "profileImageUrl": null,
  "isFromTatarstan": false
}
```

Success response: `201 Created`

Example response:
```json
{
  "token": "jwt-token-here"
}
```

---

## Authenticated user endpoints

### 7. Get current user profile
- Method: `GET`
- Path: `/auth/me`
- Purpose: Get the profile of the currently authenticated user.
- Auth required: Yes
- Headers:
  - `Authorization: Bearer <token>`
- Success response: `200 OK`

Example response:
```json
{
  "id": 1,
  "username": "alice",
  "email": "alice@example.com",
  "profileImageUrl": null,
  "isFromTatarstan": false,
  "userRole": "USER"
}
```

---

### 8. Create a game
- Method: `POST`
- Path: `/games`
- Purpose: Create a new game owned by the authenticated user.
- Auth required: Yes
- Headers:
  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`
- Body:
```json
{
  "title": "My Game",
  "description": "A short description",
  "bannerUrl": "https://example.com/banner.png",
  "gameTags": ["RPG", "Adventure"],
  "screenshots": {
    "videos": [],
    "pictures": []
  }
}
```

Success response: `201 Created`

Example response:
```json
{
  "id": 10,
  "authorId": 1,
  "title": "My Game",
  "description": "A short description",
  "bannerUrl": "https://example.com/banner.png",
  "createdAt": "2026-01-02T10:00:00Z",
  "updatedAt": "2026-01-02T10:00:00Z",
  "isOwner": true,
  "isApproved": false,
  "author": {
    "username": "alice",
    "profile_image_url": null,
    "email": "alice@example.com"
  },
  "gameTags": {
    "GENRE": ["RPG", "Adventure"]
  },
  "screenshots": {
    "videos": [],
    "pictures": []
  }
}
```

---

### 9. Update a game
- Method: `PATCH`
- Path: `/games/{id}`
- Purpose: Update an existing game.
- Auth required: Yes
- Headers:
  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`
- Body:
```json
{
  "title": "Updated title",
  "description": null,
  "bannerUrl": null,
  "gameTags": ["Strategy"],
  "screenshots": null
}
```

Notes:
- All fields are optional.
- Passing `null` leaves the field unchanged.
- If `gameTags` is provided, the current tags are replaced.

Success response: `200 OK`

Example response:
```json
{
  "id": 10,
  "authorId": 1,
  "title": "Updated title",
  "description": "A short description",
  "bannerUrl": "https://example.com/banner.png",
  "createdAt": "2026-01-02T10:00:00Z",
  "updatedAt": "2026-01-02T10:30:00Z",
  "isOwner": true,
  "isApproved": false,
  "author": {
    "username": "alice",
    "profile_image_url": null,
    "email": "alice@example.com"
  },
  "gameTags": {
    "GENRE": ["Strategy"]
  },
  "screenshots": {
    "videos": [],
    "pictures": []
  }
}
```

---

### 10. Delete a game
- Method: `DELETE`
- Path: `/games/{id}`
- Purpose: Delete a game owned by the authenticated user.
- Auth required: Yes
- Headers:
  - `Authorization: Bearer <token>`
- Success response: `204 No Content`

---

## Admin endpoints

### 11. Get all users
- Method: `GET`
- Path: `/admin/users`
- Purpose: Retrieve the list of users.
- Auth required: Admin role
- Headers:
  - `Authorization: Bearer <token>`
- Success response: `200 OK`

Example response:
```json
{
  "users": [
    {
      "id": 1,
      "username": "alice",
      "email": "alice@example.com",
      "userRole": "USER"
    }
  ]
}
```

---

### 12. Promote user to admin
- Method: `POST`
- Path: `/admin/users/promote`
- Purpose: Promote a normal user to admin.
- Auth required: Admin role
- Headers:
  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`
- Body:
```json
{
  "userId": 2
}
```

Success response: `200 OK`

---

### 13. Demote admin to user
- Method: `POST`
- Path: `/admin/users/demote`
- Purpose: Remove admin privileges from a user.
- Auth required: Admin role
- Headers:
  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`
- Body:
```json
{
  "userId": 2
}
```

Success response: `200 OK`

---

### 14. Ban user
- Method: `POST`
- Path: `/admin/users/ban`
- Purpose: Ban a user.
- Auth required: Admin role
- Headers:
  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`
- Body:
```json
{
  "userId": 2
}
```

Success response: `200 OK`

---

### 15. Unban user
- Method: `POST`
- Path: `/admin/users/unban`
- Purpose: Remove a ban from a user.
- Auth required: Admin role
- Headers:
  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`
- Body:
```json
{
  "userId": 2
}
```

Success response: `200 OK`

---

### 16. Delete user
- Method: `DELETE`
- Path: `/admin/users/{id}`
- Purpose: Delete a user account.
- Auth required: Admin role
- Headers:
  - `Authorization: Bearer <token>`
- Success response: `200 OK`

---

### 17. Approve game
- Method: `PATCH`
- Path: `/admin/games/{id}/approve`
- Purpose: Approve a pending game.
- Auth required: Admin role
- Headers:
  - `Authorization: Bearer <token>`
- Success response: `200 OK`

---

### 18. Reject game
- Method: `PATCH`
- Path: `/admin/games/{id}/reject`
- Purpose: Reject a pending game.
- Auth required: Admin role
- Headers:
  - `Authorization: Bearer <token>`
- Success response: `200 OK`

---

## Comment endpoints

Comments are nested under a game. The base path is `/games/{game_id}/comments`.

### 19. Get all comments of a game
- Method: `GET`
- Path: `/games/{game_id}/comments`
- Purpose: Retrieve a paginated list of comments for a game, ordered by creation date descending.
- Auth required: No
- Query parameters:
  - `page` (optional, default `0`)
  - `size` (optional, default `5`)
- Success response: `200 OK`
- Response body: a paginated Spring Data `Page` object containing an array of comments under `content`

Example response:
```json
{
  "content": [
    {
      "id": 1,
      "author": {
        "username": "alice",
        "profile_image_url": null,
        "email": "alice@example.com"
      },
      "text": "Great game!",
      "createdAt": "2026-01-02T10:00:00Z",
      "updatedAt": "2026-01-02T10:00:00Z"
    }
  ],
  "totalElements": 1
}
```

---

### 20. Create a comment
- Method: `POST`
- Path: `/games/{game_id}/comments`
- Purpose: Create a new comment for a game on behalf of the authenticated user.
- Auth required: Yes
- Headers:
  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`
- Body:
```json
{
  "text": "Great game!"
}
```

Notes:
- `text` is required and must be non-blank.

Success response: `201 Created`

Example response:
```json
{
  "id": 1,
  "author": {
    "username": "alice",
    "profile_image_url": null,
    "email": "alice@example.com"
  },
  "text": "Great game!",
  "createdAt": "2026-01-02T10:00:00Z",
  "updatedAt": "2026-01-02T10:00:00Z"
}
```

---

### 21. Update a comment
- Method: `PATCH`
- Path: `/games/{game_id}/comments/{comment_id}`
- Purpose: Update the text of an existing comment.
- Auth required: Yes
- Headers:
  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`
- Body:
```json
{
  "text": "Updated comment text"
}
```

Notes:
- `text` is required and must be non-blank.

Success response: `200 OK`

Example response:
```json
{
  "id": 1,
  "author": {
    "username": "alice",
    "profile_image_url": null,
    "email": "alice@example.com"
  },
  "text": "Updated comment text",
  "createdAt": "2026-01-02T10:00:00Z",
  "updatedAt": "2026-01-02T10:30:00Z"
}
```

---

### 22. Delete a comment
- Method: `DELETE`
- Path: `/games/{game_id}/comments/{comment_id}`
- Purpose: Delete an existing comment. The author of the comment or an admin can delete it.
- Auth required: Yes
- Headers:
  - `Authorization: Bearer <token>`
- Success response: `204 No Content`
- Response body: the deleted comment object

Example response:
```json
{
  "id": 1,
  "author": {
    "username": "alice",
    "profile_image_url": null,
    "email": "alice@example.com"
  },
  "text": "Great game!",
  "createdAt": "2026-01-02T10:00:00Z",
  "updatedAt": "2026-01-02T10:00:00Z"
}
```

---

## Notes
- The following endpoints are publicly accessible:
  - `GET /games`
  - `GET /games/{id}`
  - `GET /games/author/{id}`
  - `GET /games/tags/all`
  - `GET /games/{game_id}/comments`
  - `POST /auth/login`
  - `POST /auth/register`

- The following require authentication:
  - `GET /auth/me`
  - `POST /games`
  - `PATCH /games/{id}`
  - `DELETE /games/{id}`
  - `POST /games/{game_id}/comments`
  - `PATCH /games/{game_id}/comments/{comment_id}`
  - `DELETE /games/{game_id}/comments/{comment_id}`

- Admin-only routes require a JWT with the `ADMIN` role.
- The empty forum and store controllers currently do not expose any public REST endpoints.
