# Identified roles
1. **Game Developer**: A creator who wants to showcase their games, manage their profile, and recruit team members.
2. **Site Developer**: The technical role responsible for maintaining the site, parsing external data, and ensuring system functionality.
3. **Customer**: Person who dedicates the project's requirements.
4. **Community User:** A visitor or registered member of the community. Focuses on consuming content, networking, and interacting with the platform.
5. ** Admin/Moderator:** User who can edit cards, choose featured games, see events proposals, addifng merch to the shop, edit or delete events.

---

# User stories

## US-01: Parse Saturday Screenshot posts to game page

**Requirement status:** Active
**MoSCoW priority:** Could have

As a Site Admin,
I want to parse "Saturday Screenshot" posts into game pages,
so that game information is updated automatically without manual data entry.

### Notes and constraints
- Requires integration with Telegram API or RSS feed parsing.
- Parsing logic must handle image extraction and basic text formatting.

---

## US-02: Add game cards from developer profile

**Requirement status:** Active
**MoSCoW priority:** Must Have

As a Game Developer,
I would like to add game cards with brief info from my profile,
so that I can easily showcase my projects to the community.

### Notes and constraints
- Should reuse existing profile data (e.g., developer name, contacts) to pre-fill the game card form.

---

## US-03: Side tab for Telegram event parsing and notifications

**Requirement status:** Active
**MoSCoW priority:** Must Have

As a Community User,
I want to see a side tab with event posts and their changes over time with notifications,
so that I stay updated on community events and schedule changes.

### Notes and constraints
- Changes over time imply a version history or "updated" badge on the event.

---

## US-04: Project information and collaborator page

**Requirement status:** Active
**MoSCoW priority:** Must Have

As the Customer,
I want a page with information about the project and info for collaborators,
so that we can clearly communicate our mission and attract partners.

### Notes and constraints
- Should include clear calls-to-action

---

## US-05: Comprehensive user profile

**Requirement status:** Active
**MoSCoW priority:** Must Have

As a Community User,
I want to see a user profile with all the game cards, contact information, and forum activity,
so that I can learn about other members and network effectively.

### Notes and constraints
- Privacy settings should allow users to hide contact information if desired.

---

## US-06: Game rating system and view count

**Requirement status:** Active
**MoSCoW priority:** Should Have

As a Community User,
I would like to see a rating system and view count for each game,
so that I can discover popular and highly-rated projects.

### Notes and constraints
- Rating can be a simple 5-star system or upvote/downvote mechanism.
- View count should be protected against basic bot inflation.

---

## US-07: Forum for project proposals and recruitment

**Requirement status:** Active
**MoSCoW priority:** Should Have

As a Community User,
I would like to see a forum for future project proposals and team recruitment,
so that I can find collaborators to develop a game.

### Notes and constraints
- Should support basic categorization (e.g., "Looking for Artist", "Looking for Programmer").

--- 

## US-08: Unified brandbook page

**Requirement status:** Removed
**Previous MoSCoW priority:** Could Have

As the Customer,
I would like to see a brandbook with a unified design,
so that the project maintains a professional visual identity.

**Reason:** Removed. A brandbook is an internal design artifact and a guideline for the development team, not a functional end-user feature. The visual guidelines should simply be applied to the site's UI rather than published as a separate user-facing page.

---

## US-09: Regional emphasis on Tatarstan users

**Requirement status:** Active
**MoSCoW priority:** Must Have

As the Customer,
I want the platform to emphasize users and inventory from Tatarstan,
so that we fulfill our regional mission and support the local community.

### Notes and constraints
- This may require a geolocation filter or a dedicated "Local" tag for users and inventory items.

---

## US-10: Informative welcome page

**Requirement status:** Active
**MoSCoW priority:** Must Have

As the Customer,
I want an engaging welcome page with brief project info, game cards, and contacts,
so that we effectively onboard new visitors and communicate the community's value.

### Notes and constraints
- This is the landing page. Performance and load time are critical here.

---

## US-11: Purple + green visual style with media backgrounds

**Requirement status:** Active
**MoSCoW priority:** Must Have

As the Customer,
I want the site to use a purple visual style with video and photo backgrounds on the main page,
so that the design strictly aligns with our branding guidelines.

### Notes and constraints
- Background videos must be optimized for performance to avoid slowing down the welcome page (US-11).

--- 

## US-12: Merch selling via gamification

**Requirement status:** Removed
**Previous MoSCoW priority:** Could Have

As the Customer,
I would like to see merch selling integrated with gamification elements,
so that we increase user engagement and sales.

---

## US-13: Integrated on-site merch shop

**Requirement status:** Active
**MoSCoW priority:** Must Have

As the Customer,
I want the merch shop to be fully integrated inside the site,
so that we maintain a seamless user journey and keep traffic on our domain.

### Notes and constraints
- Must provide a seamless UX, keeping the user within the site's domain during the browsing phase.

---

## US-14: Quick project addition from main page

**Requirement status:** Active
**MoSCoW priority:** Must Have

As a Game Developer,
I want the ability to add projects from the main page via a "plus" button,
so that I can quickly submit a new game without navigating through multiple menus.

### Notes and constraints
- The button should be visible only to authenticated users with game developer privileges.

---

## Initial proposed MVP v1 scope

The following small, non-empty subset of **Active Must Have** user stories is proposed for the initial MVP v1. This scope focuses on the core value proposition: presenting the community, allowing basic project submission, and establishing user profiles.

- **US-05**: Comprehensive user profile
- **US-10**: Informative welcome page

*(Note: This is an initial proposal for prototyping and discussion. It will be refined, estimated, and finalized in Assignment 3.)*