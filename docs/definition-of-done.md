## Definition of done requirements

- All issue acceptance criteria and sprint scope items are satisfied, including the relevant MVP v2 user-visible and technical goals.
- The work is reviewed by another team member and the review evidence is available in the normal pull request workflow.
- For user stories, the linked supporting PBIs provide the required implementation, review, and verification evidence.
- Required CI checks for the product stack are green: frontend lint, frontend tests, frontend build, backend Maven verification, additional QA checks, frontend-backend integration, Docker Compose smoke testing, and documentation link checks.
- Required automated quality requirement tests for the changed functionality are green.
- Coverage expectations are met for the critical modules affected by the sprint scope, especially authentication, game management, public author access, and deployment/proxy behavior.
- Verification evidence is preserved in the normal workflow artifacts, including test reports, build artifacts, Playwright output, and Docker Compose logs where relevant.
- Architecture changes, deployment-model changes, and CI configuration changes are reflected in the ADR set and the maintained documentation so the completion standard stays current.
- Pull requests and commits have meaningful names.
- Changelog is updated for user-visible changes.