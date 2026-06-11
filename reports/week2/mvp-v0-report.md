The prototype and MVP v0 have different purposes and evaluation criteria, but the same implementation may serve both roles if it satisfies both sets of requirements:

* The prototype explores and communicates the proposed MVP v1 user experience. It does not need production behavior.
* MVP v0 is a runnable or deployed technical product foundation with a working smoke check. It does not need to implement a complete user story or reproduce the prototype.
* MVP v0 may reuse or implement elements of the prototype where useful.
* Runnable prototype code, including a static application such as TypeScript generated from a Figma prototype, may also serve as MVP v0 when it is accessible over the internet, usable for its demonstrated purpose, and satisfies the MVP v0 smoke-check requirements.

The table below summarises the different purposes and criteria:

| Aspect                                        | Prototype                                                   | MVP v0                                            |
| --------------------------------------------- | ----------------------------------------------------------- | ------------------------------------------------- |
| **Purpose**                                   | Explore and communicate the proposed MVP v1 user experience | Establish a runnable technical product foundation |
| **Primary audience**                          | Team, customer (for feedback)                               | TA (for evaluation)                               |
| **Needs customer approval?**                  | No                                                          | No                                                |
| **Needs production behavior?**                | No                                                          | No                                                |
| **Needs to implement a complete user story?** | No                                                          | No                                                |
| **Must be runnable / deployed?**              | No (interactive mock is sufficient)                         | Yes                                               |
| **Must pass a smoke check?**                  | No                                                          | Yes                                               |
| **May use mocks / placeholders?**             | Yes                                                         | Yes                                               |

MVP v0 is evaluated by the TA. It does not need to be shown to or approved by the customer.

1. Build a runnable or deployed MVP v0 product foundation. A complete end-to-end business feature is not required; placeholder behavior and mocks are allowed where appropriate.

2. Document MVP v0 in:

   ```text
   reports/week2/mvp-v0-report.md
   ```

   Include:

   * Purpose and description of the MVP v0 foundation
   * Deployment URL or runnable-artifact link
   * Public video demonstration link
   * Relationship to the prototype and proposed MVP v1 stories, where applicable
   * Current limitations, placeholders, and mocks
   * Link to local setup instructions
   * Repeatable smoke-check scenario

3. Define and document at least one repeatable smoke-check scenario in `reports/week2/mvp-v0-report.md`. Include access instructions, steps, and expected results. The smoke check must demonstrate that MVP v0 is accessible and usable for its demonstrated purpose. Use a scenario appropriate to the product:

   * **Web/mobile:** the application opens, primary navigation works, and at least one interactive data-flow element is demonstrated (e.g., form submission, API call, or state change).
   * **API/service:** the service starts, a health endpoint works, and Swagger UI is accessible when the API is part of the documented external interface.
   * **CLI:** the command runs and `--help` works.
   * **Library:** the package builds and a minimal usage example runs.
   * **Other product type:** provide an equivalent minimal check demonstrating that the product foundation runs.

4. Hosted products must be accessible over the internet until the course has been graded.

5. For a mobile application, provide either:

   * A publicly accessible hosted web/emulator build, or
   * An accessible installable build or test-distribution link with clear installation and access instructions.

   A video demonstration alone does not satisfy the MVP v0 access requirement.

6. For a CLI tool, library, or another product that cannot reasonably be hosted as a running service, publish an accessible runnable artifact or package and provide clear run instructions.

7. Provide a public sanitized video demonstration of MVP v0 shorter than two minutes.

8. Provide clear and reproducible local setup instructions in the root `README.md`. Link from the root `README.md` to `reports/week2/README.md` and `reports/week2/mvp-v0-report.md`.

9. In the Moodle PDF, explain exactly how the TA can access MVP v0. Include the deployment URL or runnable-artifact link, dedicated limited-permission test credentials if needed, and a link to the documented smoke-check scenario.

> [!IMPORTANT]
> Never submit real, personal, or production credentials. This includes API keys, tokens, passwords, and other secrets — even sandbox or test credentials from third-party services. Add a sanitized `.env.example` file and ensure `.env` and other secret files are listed in `.gitignore`.
