# Sprint 4 Retrospective

**Date:** 11.07.2026  
**Participants:** @Son-Go, @the-shtorm, @venimu, @Zhend0sss, @grishinegor44-creator

## What went well

1. **User moderation functionality:** The team implemented user roles and administrative moderation actions, including banning and unbanning users. This made the platform safer and gave administrators control over inappropriate user activity.
2. **Comments section:** The team implemented comments functionality, including deleting comments of banned users.
3. **Authorization improvements:** The team added restrictions for banned users. Banned users can no longer create, edit, or delete games and comments, while normal authorized users can continue using these features.
4. **Documentation preparation:** The team updated the main repository documentation and started preparing Assignment 6 deliverables, including customer handover guidance, contributor instructions, agent guidance, roadmap updates, and user acceptance tests.

## What did not go well

1. **Testing process:** Some functionality required several manual Postman checks because authorization, user roles, and ban restrictions affect multiple endpoints. During development, some validation calls were accidentally missed after refactoring and had to be restored.
2. **Task traceability:** Not every implemented Sprint 4 task had a dedicated GitHub issue at the beginning of the sprint. This makes it harder to link roadmap items, Pull Requests, testing evidence, and completed work.
3. **Documentation consistency:** Several documentation files required updates at the same time. The team needed to ensure that README, roadmap, UATs, API documentation, customer handover documentation, and implementation state did not contradict each other.

## Action points

1. Create or update GitHub issues for all Sprint 4 moderation, authorization, testing, and documentation tasks, then link them to the relevant milestone and Pull Requests.
2. Add and maintain negative test cases for restricted actions, including attempts by banned users to create comments, create games, edit games, or delete content.
3. Complete the Assignment 6 documentation review before Sprint 5: verify all links, production access instructions, local setup commands, known limitations, troubleshooting steps, and customer handover status.
4. Use a documentation checklist before merging changes that affect API endpoints, environment variables, deployment, authentication, user permissions, or customer-facing functionality.
