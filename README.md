# SWP_team30 GDE website

## Description
GDE Website - website for GDE innopolis club, where local game developers can publish their indie games and russian gamer can explore new games

---

## Access instructions

To access deployed MVP-v0 open [link](https://precious-toffee-52e805.netlify.app/)
To access deployed MVP-v1 open [link](http://193.187.95.33)

---

## Local setup instructions

**MVP-v0:** Open https://github.com/Son-Go/SWP_team30/tree/8c2e78c353f0c7f9470b3c63cf59da54a8ecf47e/temp-frontend/htmlFrontend folder, and run VS code Live server extension. MVP-v0 has no backend functionality
**MVP-v1:** Clone project, run docker compose up. `http://localhost:80` will show working website

---

## Weekly README reports

- Week 2 [README.md](https://github.com/Son-Go/SWP_team30/blob/8b765b5d98da638555c6fb2a9f5be49d44b7cc38/reports/week2/README.md)
- Week 3 [README.md](https://github.com/Son-Go/SWP_team30/blob/1214b680442a30e2ae7adfb5d748f33b7229d92b/reports/week3/README.md)

---

## Visuals

---

## Usage

---

## Endpoints testing
1) Open Postman
2) Create new Collection for example `SWP API test`

### Games endpoints

#### GET getAllGames
1) Create new GET request inside created collection
2) Enter url: `http://localhost:8080/games`
3) Send request

*Note:* Initially game?page is equal to 0, you can send request to the
`http://localhost:8080/games?page=1` to see list of games on the next page

*Expected response:* raw, JSON pageable list of all games with `code 200`

#### GET getGameById
1) Create new GET request inside created collection
2) Enter url: `http://localhost:8080/games/{id}`
3) Open Authorization header and choose Auth type: `Bearer token`
4) Insert user token
5) Send request
6) Repeat process from `3` and delete token for check isOwner field

*Expected response:* `Code 200`, raw JSON response with body of requested game.
field isOwner: true if authentication token same as game author token, false otherwise.

#### POST createGame
1) Create new POST request inside created collection
2) Enter url: `http://localhost:8080/games`
3) Open Authorization header and choose Auth type: `Bearer token`
4) Insert user token
5) Send request
6) Repeat process from `3` and delete token for check with not authenticated user

*Expected response:* `Code 201`, raw JSON response with body of created game.
`Code 401` if user is not authorized.

#### PATCH updateGame
1) Create new POST request inside created collection
2) Enter url: `http://localhost:8080/games/{id}`
3) Open Authorization header and choose Auth type: `Bearer token`
4) Insert user token
5) Send request
6) Repeat process from `3` and delete token for check with not authenticated user

*Expected response:* `Code 200`, raw JSON response with body of updated game.
`Code 401` if user is not authorized.
`Code 404` if game with requested id not found. `Code 403` if user permissions are denied.

#### DELETE deleteGame
1) Create new POST request inside created collection
2) Enter url: `http://localhost:8080/games/{id}`
3) Open Authorization header and choose Auth type: `Bearer token`
4) Insert user token
5) Send request
6) Repeat process from `3` and delete token for check with not authenticated user

*Expected response:* `Code 204`, raw JSON response without body.
`Code 401` if user is not authorized.
`Code 404` if game with requested id not found. `Code 403` if user permissions are denied.

### Authentication endpoints

#### POST register
1) create new POST request  inside created collection
2) enter url: `http://localhost:8080/auth/register`
3) Open body header
4) Chose raw, JSON format
5) Insert info about new user: username, email, password, banner image (can be null) 
6) Send request

*Expected response:* raw JSON format. `Code 201` with registered user token inside body of the response.
`Code 409` if email is already used.

#### POST login
1) create new POST request  inside created collection
2) enter url: `http://localhost:8080/auth/login`
3) Open body header
4) Chose raw, JSON format
5) Insert info about user: email, password
6) Open Authorization header and choose Auth type: `Bearer token`
7) Insert user generated token
8) Send request

*Expected response:* raw, JSON, `Code 200` with log-inned user token inside response body.
`Code 401` if email does not exist. `Code 401` if entered password is incorrect.

#### GET me
1) create new GET request  inside created collection
2) enter url: `http://localhost:8080/auth/me`
3) Open Authorization header and choose Auth type: `Bearer token`
4) Insert user generated token
5) Send request

*Expected response:* raw, JSON.`Code 200` with info about user inside body of response.
`Code 401` if user token is invalid. `Code 401` if user does not exist

---

## License
[LICENCE](https://github.com/Son-Go/SWP_team30/blob/8b765b5d98da638555c6fb2a9f5be49d44b7cc38/LICENSE)

---

## Project status
**Active development**

---
