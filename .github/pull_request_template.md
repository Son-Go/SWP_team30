## Description
<!-- Provide a clear and concise description of what this PR does and the business value it delivers. -->

## Related Issues
<!-- Link to the Jira/GitHub issue(s) this PR addresses. Use keywords like "Closes #123" or "Relates to PROJ-456". -->

## Type of Change
<!-- Check all that apply. -->
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Database change (Schema update, Flyway/Liquibase migration)
- [ ] Refactoring (no functional changes, no api changes)
- [ ] Build/CI or Documentation update

## Areas Affected
<!-- Check the layers of the application impacted by this PR. -->
- [ ] **Frontend** (UI/UX, State Management, Routing)
- [ ] **Backend** (REST Controllers, Services, Repositories)
- [ ] **Database** (Tables, Indexes, Migrations)
- [ ] **Infrastructure/Config** (Docker, application.yml, Environment Variables)

## How to Test
<!-- Provide step-by-step instructions for the reviewer to test this manually. This is crucial for cross-stack testing! -->
**Backend / API Testing:**
1. 
2. 

**Frontend / UI Testing:**
1. 
2. 

**Automated Tests:**
- [ ] Added/Updated Unit Tests (JUnit / Jest / etc.)
- [ ] Added/Updated Integration / E2E Tests

## Screenshots / Recordings (If applicable)
<!-- If this includes UI changes, please attach before/after screenshots or a short Loom/video link. -->

---

## Enterprise Checklist

### General
- [ ] My code follows the style guidelines of this project (Checkstyle/Prettier/ESLint).
- [ ] I have performed a self-review of my own code.
- [ ] I have commented my code, particularly in hard-to-understand areas.
- [ ] All CI/CD pipeline checks (build, linting, automated tests) pass successfully.

### Backend (Java / Spring)
- [ ] No N+1 query issues or unoptimized database calls.
- [ ] Proper exception handling and standardized error responses.
- [ ] Sensitive data is not logged (no passwords, PII, or tokens in logs).
- [ ] Database migrations (Flyway/Liquibase) are backward-compatible and tested.

### Frontend
- [ ] UI is responsive and tested across required browsers/devices.
- [ ] Accessibility (a11y) standards are met (e.g., ARIA labels, keyboard navigation).
- [ ] No hardcoded strings (using i18n/localization if applicable).
- [ ] State management and API calls handle loading, error, and empty states gracefully.

### Security & Performance
- [ ] No hardcoded secrets, API keys, or credentials in the code.
- [ ] Input validation and sanitization are in place (preventing XSS/SQLi).
- [ ] Considered the performance impact (e.g., pagination, lazy loading, caching).