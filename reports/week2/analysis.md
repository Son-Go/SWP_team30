## Learning points

### From writing user stories
- **Role clarity is critical**: Initially we had overlapping roles (Game Developer vs Community User), which required refinement. We learned that clear persona definitions help write more focused stories.
- **MoSCoW prioritization reveals scope risks**: We identified 9 "Must Have" stories, which is too large for MVP v1. This taught us that prioritization must be aggressive to create a feasible MVP.
- **Removed stories need justification**: US-08 (brandbook page) and US-12 (merch gamification) were removed with clear reasoning, showing that not all customer ideas become product features.

### From prioritization
- **Must Have overload**: With 9 Must Have stories, we learned that customer expectations often exceed realistic MVP scope. We proposed only US-05 and US-10 for initial MVP v1 to keep it small and prototype-able.
- **Regional mission affects priority**: US-09 (Tatarstan emphasis) is Must Have because it's core to the customer's mission, not just a feature preference.


### From creating figma interactive prototype
- **Navigation complexity**: The side tab for Telegram events (US-03) requires careful UI design to not clutter the main interface.
- **Purple + green styling is challenging**: US-11's visual requirement (purple primary with green accents + video backgrounds) needs careful colour balance in the prototype to avoid visual noise.

### From interface design
- **Profile page information density**: US-05's comprehensive profile needs to show game cards, contacts, and forum activity without overwhelming users. Privacy settings (hiding contacts) add UI complexity.
- **Welcome page performance**: US-10's landing page with video backgrounds (US-11) must balance visual appeal with load time—critical for first impressions.
- **Merch shop integration**: US-13 requires the shop to feel native to the site, not a separate external system.

---
## Validated assumptions

### Confirmed through prototype and customer meeting
| Assumption                                                                      | Validation | Evidence |
|---------------------------------------------------------------------------------|------------|----------|
| Users need to see game cards from the Tatarstan prominently on the welcome page | **Confirmed** | Customer approved US-10 prototype with game card grid |
| Game Developers want quick project addition without navigation                  | **Confirmed** | Customer confirmed US-14's "plus" button is valuable |
| Privacy settings for contact information are necessary                          | **Confirmed** | Customer agreed US-05's privacy constraint is important |
| Purple + green visual style aligns with brand identity                          | **Confirmed** | Customer explicitly approved US-11 prototype styling |
| Tatarstan regional emphasis is core to the mission                              | **Confirmed** | Customer repeatedly emphasized US-09 as non-negotiable |

### Confirmed through MVP v0 technical work
| Assumption                                | Validation | Evidence                                                    |
|-------------------------------------------|------------|-------------------------------------------------------------|
| User needs navigation on top bar panel    | **Confirmed** | During MVP v0 navigation on top bar works succesfully       |
| Customer need purple + green visual style | **Confirmed** | Purple + green visual style is presented on prototype pages |

### Rejected through prototype or testing
| Assumption | Rejection | Evidence                                                    |
|------------|-----------|-------------------------------------------------------------|
| Brandbook should be a user-facing page | **Rejected** | Removed US-08—brandbook is internal, not end-user feature   |
| Merch gamification is needed for MVP | **Rejected** | Customer rejected selling merch via gamification.           |
| Side tab for Telegram events won't clutter UI | **Rejected/Needs revision** | Customer found US-03 side tab visually crowded in prototype |
| 5-star rating system is preferred | **Rejected** | Customer prefers simple comments over US-06's 5-star        |

---

## Needs clarification

### Unresolved questions

1. **Geolocation implementation for US-09**:
    - Should we use IP-based geolocation, user-provided location, or manual "Local" tag?
    - What accuracy level is acceptable (city vs region vs country)?
    - *Risk*: Privacy concerns with IP-based tracking.

2. **Forum categorization for US-07**:
    - What are the exact categories needed (e.g., "Looking for Artist", "Looking for Programmer", "Project Proposal")?

### Unresolved requirements

1. **Profile privacy levels for US-05**:
    - What exactly can users hide (contact info only, or also game cards, forum activity)?
    - Are there public vs private profile modes?

2. **Event version history for US-03**:
    - How many versions should be stored?
    - Should users see all historical versions or just current + "updated" badge?

### Technical risks

1. **Telegram parsing reliability for US-01**:
- What happens if Telegram API is unavailable or returns malformed data?
- *Mitigation*: Add error handling and fallback to manual entry.

2. **Database performance for US-05 profiles**:
- Querying game cards + forum activity for each profile could be slow.
- *Mitigation*: Use indexed queries and caching.

3. **MoSCoW scope creep**:
- With 9 Must Have stories, MVP v1 scope may expand beyond feasible timeline.
- *Mitigation*: Strictly enforce Initial proposed MVP v1 scope (US-05, US-10) in Assignment 3.

---

## Planned response

### How learning points affect MVP v1

| Learning point                                      | Impact on MVP v1                                                                                                      | Affected stories/artifacts                        |
|-----------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|---------------------------------------------------|
| 9 Must Have stories is too large                    | **Scope reduction**: MVP v1 will start with only US-05 + US-10, then incrementally add other Must Haves               | `user-stories.md` → Initial proposed MVP v1 scope |
| Customer prefers comment raiting system over 5-star | **Rating system change**: US-06 will use comments system instead of 5-star                                            | `user-stories.md` → US-06 notes                   |
| Geolocation method unclear                          | **Customer decision**: Assignment 3 meeting will ask customer to choose between IP-based vs manual location for US-09 | `customer-meeting-summary.md` → Action point      |
| Privacy settings scope unclear                      | **Requirement clarification**: Define exact privacy fields for US-05 before MVP v1 implementation                     | `user-stories.md` → US-05 notes                   |
| MoSCoW scope creep risk                             | **Strict prioritization**: Assignment 3 estimation will reject any new Must Have additions without customer approval  | `user-stories.md` → MoSCoW priority               |

### MVP v1 scope refinement plan

**Current proposed scope**: US-05 (Comprehensive user profile) + US-10 (Informative welcome page)

**Expected additions in Assignment 3** (after refinement and estimation):
- US-02 (Add game cards from developer profile) — Must Have, core to Game Developer value
- US-14 (Quick project addition from main page) — Must Have, complements US-02

**Stories deferred to MVP v2** (still Must Have, but out of initial MVP v1 scope):
- US-03 (Side tab for Telegram events) — needs UI revision first
- US-09 (Tatarstan emphasis) — requires geolocation technical spike
- US-13 (Integrated merch shop) — complex payment integration
- US-04 (Project information page) — can be simplified static page later

### Action items for Assignment 3

1. **Updating US-06 rating system** from 5-star to comments system in user-stories.md
2. **Customer meeting to decide** geolocation method for US-09
3. **Performance optimization** for US-10 + US-11 (video compression, fallback image)
4. **Privacy requirements clarification** for US-05 (which fields can be hidden)

---

**Note:** *This analysis will be updated after Assignment 3 estimation and final MVP v1 scope approval.*