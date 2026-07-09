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
- Success response: `200 OK`
- Response body: a paginated Spring Data `Page` object containing an array of games under `content`

Example response:
```json
{
  "content": [
    {
      "id": 1,
      "authorId": 2,
      "title": "Example Game",
      "description": "A sample game",
      "bannerUrl": "https://example.com/banner.png",
      "author": {
        "username": "alice",
        "profile_image_url": null,
        "email": "alice@example.com"
      },
      "isApproved": true,
      "gameTags": {
        "GENRE": ["RPG", "Adventure"]
      }
    }
  ],
  "totalElements": 1
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

## Notes
- The following endpoints are publicly accessible:
  - `GET /games`
  - `GET /games/{id}`
  - `GET /games/author/{id}`
  - `GET /games/tags/all`
  - `POST /auth/login`
  - `POST /auth/register`

- The following require authentication:
  - `GET /auth/me`
  - `POST /games`
  - `PATCH /games/{id}`
  - `DELETE /games/{id}`

- Admin-only routes require a JWT with the `ADMIN` role.
- The empty forum and store controllers currently do not expose any public REST endpoints.
