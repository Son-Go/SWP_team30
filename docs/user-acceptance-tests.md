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
| **Linked PBIs** | [#99 (Implement /auth/register endpoint)](https://github.com/Son-Go/SWP_team30/issues/99), [#99 (Implement /auth/login endpoint)](https://github.com/Son-Go/SWP_team30/issues/100) |

### Steps

| # | Action | Expected Outcome |
|---|---|---|
| 1 | Press to "Войти" button | Login window opens |
| 2 | Click the "Создать аккаунт" button. | Register form appears |
| 3 | Register with arbitrary credentials | Login window appears again |
| 4 | Login with same credentials | User is redirected to game page, "Войти" button changes to "Выйти" |

### Execution History

#### Week 4 — Sprint 2

| Field | Value |
|---|---|
| **Execution Date** | 27.06.2026 |
| **Executed By** | Customer (during Sprint Review) |
| **Result** | ✅ Pass |
| **Customer Comments** | The customer confirmed that login works correctly. Requested that login can be performed not only by email, but also by login |
| **Resulting PBIs** | [#183 (Login by login)](https://github.com/Son-Go/SWP_team30/issues/183) — new PBI created based on customer feedback. |

---

## UAT-002 — User can create and edit game

| Field | Value |
|---|---|
| **ID** | `UAT-002` |
| **Status** | `Active` |
| **User Goal** | As a user, I want to create and edit my games in game page, so everybody can see my games |
| **Preconditions** | The user has an account, is logged in, and the application is deployed and accessible |
| **Linked PBIs** | [ #74 PBI (Create /game CRUD endpoints )](https://github.com/Son-Go/SWP_team30/issues/74)|

### Steps

| # | Action | Expected Outcome |
|---|---|---|
| 1 | Log in to the application with valid credentials | User is redirected to the game page |
| 2 | Click "Create game" button. | A game creation form appears with title, banner and description fields |
| 3 | Fill all fields with applicable data | Fields are filled without error |
| 4 | Click "Save". | User is redirected to newly created game page |
| 5 | Check the game list for newly created game | The saved game is visible in the game list with the correct title and description |
| 6 | Open game card, click "Edit" button and change some fields. Then save | The updated info is visible in the game page |

### Execution History

#### Week 4 — Sprint 4

| Field | Value |
|---|---|
| **Execution Date** | 27.06.2026 |
| **Executed By** | Customer (during Sprint Review) |
| **Result** | ✅ Pass |
| **Customer Comments** | The customer confirmed that games are created correctly. Requested that Game screenshots gallery can also display videos (youtube links) |
| **Resulting PBIs** | [#184 (add video player)](https://github.com/Son-Go/SWP_team30/issues/184) — new PBI created based on customer feedback. |

---

## UAT-003 — User can report a bug or request a feature from within the app

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

#### Week 4 — Sprint 4

| Field | Value |
|---|---|
| **Execution Date** | 27.06.2026 |
| **Executed By** | Customer (during Sprint Review) |
| **Result** | ✅ Pass |
| **Customer Comments** | Customer confirmed that tags sorting works successfully. Requested adding different tag types for better navigation between different tags |
| **Resulting PBIs** | [#185 (Different tag types)](https://github.com/Son-Go/SWP_team30/issues/185) |

---