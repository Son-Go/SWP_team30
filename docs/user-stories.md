# User Stories Index

This document serves as the authoritative index for all user stories, providing traceability between the requirements and the issue tracker.

> **Note:** The issue tracker is the authoritative source for live status. This file mirrors the current state for traceability.

> **Important** We implemented GitHub Action to autommatically assign indexes to new User stories (and indexes can be based only on issue number (it is GitHub Actions limitation)). So after first 14 incrementally indexed stories, future stories have ascending sparse indexes

## Active User Stories

Active stories are ordered by **MoSCoW priority** (Must Have → Should Have → Could Have), then by **Sprint** (unassigned `—` first), then by **stable ID**.

| ID    | Short title | MoSCoW priority | Issue                                            | Requirement status | Work Status | Sprint |
|-------|---|---|--------------------------------------------------|---|---|---|
| US-14 | Quick project addition from main page | Must Have | [#14](https://github.com/Son-Go/SWP_team30/issues/55) | Active | Done | [Sprint-1](https://github.com/Son-Go/SWP_team30/milestone/1) |
| US-61 | Informative game page | Must Have | [#61](https://github.com/Son-Go/SWP_team30/issues/61) | Active | Done | [Sprint-1](https://github.com/Son-Go/SWP_team30/milestone/1) |
| US-62 | Game info page | Must Have | [#62](https://github.com/Son-Go/SWP_team30/issues/62) | Active | Done | [Sprint-1](https://github.com/Son-Go/SWP_team30/milestone/1) |
| US-63 | Game cards | Must Have | [#63](https://github.com/Son-Go/SWP_team30/issues/63) | Active | Done | [Sprint-1](https://github.com/Son-Go/SWP_team30/milestone/1) |
| US-02 | Add game cards from developer profile | Must Have | [#2](https://github.com/Son-Go/SWP_team30/issues/45) | Active | To Do | - |
| US-03 | Side tab for Telegram event parsing and notifications | Must Have | [#3](https://github.com/Son-Go/SWP_team30/issues/46) | Active | To Do | - |
| US-04 | Project information and collaborator page | Must Have | [#4](https://github.com/Son-Go/SWP_team30/issues/47) | Active | To Do | - |
| US-05 | Comprehensive user profile | Must Have | [#5](https://github.com/Son-Go/SWP_team30/issues/48) | Active | To Do | - |
| US-09 | Regional emphasis on Tatarstan users | Must Have | [#9](https://github.com/Son-Go/SWP_team30/issues/51) | Active | To Do | - |
| US-10 | Informative welcome page | Must Have | [#10](https://github.com/Son-Go/SWP_team30/issues/52) | Active | To Do | - |
| US-11 | Purple + green visual style with media backgrounds | Must Have | [#11](https://github.com/Son-Go/SWP_team30/issues/53) | Active | To Do | - |
| US-13 | Integrated on-site merch shop | Must Have | [#13](https://github.com/Son-Go/SWP_team30/issues/54) | Active | To Do | - |
| US-06 | Game rating system | Should Have | [#6](https://github.com/Son-Go/SWP_team30/issues/49) | Active | To Do | - |
| US-07 | Forum for project proposals and recruitment | Should Have | [#7](https://github.com/Son-Go/SWP_team30/issues/50) | Active | To Do | - |
| US-01 | Parse Saturday Screenshot posts to game page | Could have | [#1](https://github.com/Son-Go/SWP_team30/issues/44) | Active | To Do | - |

## Removed User Stories

Removed stories are placed after all active stories. Stable IDs are preserved to maintain historical traceability.

| ID | Short title | MoSCoW priority | Issue | Requirement status | Work Status | Sprint |
|---|---|---|---|---|---|---|
| US-08 | Unified brandbook page | Could Have | — | Removed | — | — |
| US-12 | Merch selling via gamification | Could Have | — | Removed | — | — |

---

## Refinement & Traceability Notes

The following stories were removed during requirements refinement. Traceability has been preserved by retaining their original stable IDs and marking their requirement status as `Removed`.

- **US-08 (Unified brandbook page)**:
    - **Why it changed**: Removed. A brandbook is an internal design artifact and a guideline for the development team, not a functional end-user feature.
    - **How traceability is preserved**: The stable ID `US-08` is retained in the "Removed User Stories" table. The visual guidelines from this requirement have been absorbed into **US-11** (Purple + green visual style with media backgrounds) to ensure the design strictly aligns with branding without exposing internal documents to end-users.

- **US-12 (Merch selling via gamification)**:
    - **Why it changed**: Removed during scoping to focus on core MVP value. Gamified merch selling introduces unnecessary complexity for the initial release.
    - **How traceability is preserved**: The stable ID `US-12` is retained in the "Removed User Stories" table. The core need for merchandising is now fully addressed by **US-13** (Integrated on-site merch shop), which provides a seamless, direct user journey without the overhead of gamification mechanics.

---

## Initial Proposed MVP v1 Scope

The following small, non-empty subset of **Active Must Have** user stories is proposed for the initial MVP v1. This scope focuses on the core value proposition: presenting the community, allowing basic project submission, and establishing user profiles.

> **Note:** Website design and priorities were changed during last customer meeting. So Initial MVP-v1 scope completely differnt from actual MVP-v1 scope

- **US-05**: Comprehensive user profile
- **US-10**: Informative welcome page

*(Note: This is an initial proposal for prototyping and discussion. It will be refined, estimated, and finalized in Assignment 3.)*