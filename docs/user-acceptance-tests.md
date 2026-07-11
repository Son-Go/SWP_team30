# User Acceptance Tests

This document contains the maintained UAT scenarios for the product.
Each scenario is end-user-facing and can be executed by the customer during a recorded session.

---

## UAT-001 — User can filter tasks by status

| Field | Value |
|---|---|
| **ID** | `UAT-001` |
| **Status** | `Active` |
| **User Goal** | As a user, I want to be able to create account and then login to website  |
| **Preconditions** | User is on the website |
| **Linked PBIs** | [#99 (Implement /auth/register endpoint)](https://github.com/Son-Go/SWP_team30/issues/99), [#99 (Implement /auth/login endpoint)](https://github.com/Son-Go/SWP_team30/issues/100), [#183 (Login by login)](https://github.com/Son-Go/SWP_team30/issues/183) |

### Steps

| # | Action | Expected Outcome |
|---|---|---|
| 1 | Press to "Войти" button | Login window opens |
| 2 | Click the "Создать аккаунт" button. | Register form appears |
| 3 | Register with arbitrary credentials | Login window appears again |
| 4 | Login with same credentials (email or login) | User is redirected to game page, "Войти" button changes to "Выйти" |

### Execution History

#### Week 4 — Sprint 2

| Field | Value |
|---|---|
| **Execution Date** | 27.06.2026 |
| **Executed By** | Customer (during Sprint Review) |
| **Result** | ✅ Pass |
| **Customer Comments** | The customer confirmed that login works correctly. Requested that login can be performed not only by email, but also by login |
| **Resulting PBIs** | [#183 (Login by login)](https://github.com/Son-Go/SWP_team30/issues/183) — new PBI created based on customer feedback. |

#### Week 5 — Sprint 3

| Field | Value |
|---|---|
| **Execution Date** | 04.07.2026 |
| **Executed By** | Customer (during Sprint Review) |
| **Result** | ✅ Pass |
| **Customer Comments** | The customer confirmed that login (email and login) works correctly. No comments |
| **Resulting PBIs** | No new PBI |

---

## UAT-002 — User can create game

| Field | Value |
|---|---|
| **ID** | `UAT-002` |
| **Status** | `Active` |
| **User Goal** | As a user, I want to create my games in game page, so everybody can see my games |
| **Preconditions** | The user has an account, is logged in, and the application is deployed and accessible |
| **Linked PBIs** | [ #74 PBI (Create /game CRUD endpoints )](https://github.com/Son-Go/SWP_team30/issues/74)|

### Steps

| # | Action | Expected Outcome |
|---|---|---|
| 1 | Log in to the application with valid credentials | User is redirected to the game page |
| 2 | Click "Create game" button. | A game creation form appears with title, banner and description fields |
| 3 | Fill all fields with applicable data (add screenshots and videos) | Fields are filled without error |
| 4 | Click "Save". | User is redirected to newly created game page |
| 5 | Check the game list for newly created game | The saved game is visible in the game list with the correct title and description |

### Execution History

#### Week 4 — Sprint 2

| Field | Value |
|---|---|
| **Execution Date** | 27.06.2026 |
| **Executed By** | Customer (during Sprint Review) |
| **Result** | ✅ Pass |
| **Customer Comments** | The customer confirmed that games are created correctly. Requested that Game screenshots gallery can also display videos (youtube links) |
| **Resulting PBIs** | [#184 (add video player)](https://github.com/Son-Go/SWP_team30/issues/184) — new PBI created based on customer feedback. |

#### Week 5 — Sprint 3

| Field | Value |
|---|---|
| **Execution Date** | 04.07.2026 |
| **Executed By** | Customer (during Sprint Review) |
| **Result** | ✅ Pass |
| **Customer Comments** | The customer confirmed that games are created correctly. Videos and descriptions work correctly |
| **Resulting PBIs** | No new PBI |

---

## UAT-003 — User can sort games by tags

| Field | Value |
|---|---|
| **ID** | `UAT-003` |
| **Status** | `Active` |
| **User Goal** | As a user, I want to be able to sort games by tags |
| **Preconditions** | User is on the /games page |
| **Linked PBIs** | [#174 (Adding filters by tags)](https://github.com/Son-Go/SWP_team30/issues/174) |

### Steps

| # | Action | Expected Outcome |
|---|---|---|
| 1 | Open game page (if you on other page) | game page is loaded |
| 2 | Navigate to tags menu, select prefered tags and click "Sort" | Only games with selected tags must be displayed |

### Execution History

#### Week 4 — Sprint 2

| Field | Value |
|---|---|
| **Execution Date** | 27.06.2026 |
| **Executed By** | Customer (during Sprint Review) |
| **Result** | ✅ Pass |
| **Customer Comments** | Customer confirmed that tags sorting works successfully. Requested adding different tag types for better navigation between different tags |
| **Resulting PBIs** | [#185 (Different tag types)](https://github.com/Son-Go/SWP_team30/issues/185) |

#### Week 4 — Sprint 3

| Field | Value |
|---|---|
| **Execution Date** | 04.07.2026 |
| **Executed By** | Customer (during Sprint Review) |
| **Result** | ✅ Pass |
| **Customer Comments** | Customer confirmed that tags sorting works successfully. Requested changing tags colors to make them more readable |
| **Resulting PBIs** | [#252 (Add tags' color diversity)](https://github.com/Son-Go/SWP_team30/issues/252) |

---

> New for sprint 3

## UAT-004 — User can see all games

| Field | Value |
|---|---|
| **ID** | `UAT-004` |
| **Status** | `Active` |
| **User Goal** | As a user, I want to see different games on game page |
| **Preconditions** | User is on the /games page |
| **Linked PBIs** | [#61 (Informative game page)](https://github.com/Son-Go/SWP_team30/issues/61) |

### Steps

| # | Action | Expected Outcome |
|---|---|---|
| 1 | Open game page (if you on other page) | game page is loaded |
| 2 | Navigate to "Новинки" section | Only 25 newest games are displayed |
| 3 | Navigate to "Избранное" section | Only featured by admin games are displayed |
| 3 | Navigate to "Все игры" section | All games with their tags must appear |

### Execution History

#### Week 5 — Sprint 3

| Field | Value |
|---|---|
| **Execution Date** | 04.07.2026 |
| **Executed By** | Customer (during Sprint Review) |
| **Result** | ⚠️ Semi-Pass |
| **Customer Comments** | Customer confirmed that "Новинки" and "Все игры" sections work correctly. However "Избранное" shows nothing. Also customer requested mini-profile when user howers the game card|
| **Resulting PBIs** | [#246 (Process the featured tag)](https://github.com/Son-Go/SWP_team30/issues/246), [#251 (Add the mini-profile attached to GameCard)](https://github.com/Son-Go/SWP_team30/issues/251) |

---

## UAT-005 — User can edit their games

| Field | Value |
|---|---|
| **ID** | `UAT-005` |
| **Status** | `Active` |
| **User Goal** | As a user, I want to be able to edit my games |
| **Preconditions** | User is authorized, on the /games page and own at leas one game|
| **Linked PBIs** | [ #74 PBI (Create /game CRUD endpoints )](https://github.com/Son-Go/SWP_team30/issues/74) |

### Steps

| # | Action | Expected Outcome |
|---|---|---|
| 1 | Open game page (if you on other page) | game page is loaded |
| 2 | Open page of one of your game | page with game successfully displayed |
| 3 | Click "Редактировать игру" button | Form with editable game info must appear |
| 3 | Change arbitrary fields, add screenshots or videos and click save button | New info must appear on game card |

### Execution History

#### Week 5 — Sprint 3

| Field | Value |
|---|---|
| **Execution Date** | 04.07.2026 |
| **Executed By** | Customer (during Sprint Review) |
| **Result** | ✅ Pass |
| **Customer Comments** | Customer confirmed that editing works correctly. Customer requested to improve contrast of selected tags|
| **Resulting PBIs** | [#247 (Increase the contrast of the tag selected in the filter)](https://github.com/Son-Go/SWP_team30/issues/247) |

---

> New for sprint 4

## UAT-006 — User can create a comment on a game

| Field | Value |
|---|---|
| **ID** | `UAT-006` |
| **Status** | `Active` |
| **User Goal** | As an authorized user, I want to leave a comment on a game page, so I can share my opinion about the game |
| **Preconditions** | The user has an account, is logged in, is not banned, and an existing game page is available |
| **Linked PBIs** | [#281](https://github.com/Son-Go/SWP_team30/issues/281) |

### Steps

| # | Action | Expected Outcome |
|---|---|---|
| 1 | Log in to the application with valid credentials | User is successfully authorized |
| 2 | Open the page of an existing game | The game page is displayed successfully |
| 3 | Navigate to the comments section | The comment input field and existing comments are visible |
| 4 | Enter a comment, for example: `Great game! I like the visual style and gameplay.` | The entered text is shown in the comment input field |
| 5 | Click the button to publish or send the comment | The comment is successfully created |
| 6 | Check the comments list on the game page | The new comment is visible with the entered text and the current user's name |
| 7 | Refresh the game page | The created comment remains visible after refresh |

### Execution History

#### Week 6 — Sprint 4

| Field | Value |
|---|---|
| **Execution Date** | 11.07.2026 |
| **Executed By** | Customer (during Sprint Review) |
| **Result** | ✅ Pass |
| **Customer Comments** | The customer created the comment “Great game! I like the visual style and gameplay.” on an existing game page. The comment was displayed successfully in the comments list and remained visible after the page was refreshed. No comments. |
| **Resulting PBIs** | No new PBI |
