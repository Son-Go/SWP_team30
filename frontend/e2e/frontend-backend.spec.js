import { expect, test } from "@playwright/test";

test("user can register, log in, create a game, and view it through the real stack", async ({
  page,
}) => {
  const unique = Date.now();
  const username = `e2e-user-${unique}`;
  const email = `e2e-user-${unique}@example.com`;
  const password = "E2e-password-123";
  const gameTitle = `E2E Game ${unique}`;
  const description = "Created by the frontend-backend integration test.";

  await page.goto("/auth");

  await page.locator('button[type="button"]').click();
  await page.getByLabel("Username").fill(username);
  await page.getByLabel("Email").fill(email);
  await page.getByLabel("Password").fill(password);
  await page.locator('button[type="submit"]').click();

  await expect(page.getByLabel("Username")).toBeHidden();

  await page.getByLabel("Email").fill(email);
  await page.getByLabel("Password").fill(password);
  await page.locator('button[type="submit"]').click();

  await expect(page).toHaveURL(/\/games$/);

  await page.goto("/games/create");
  await page.locator("#title").fill(gameTitle);
  await page.locator("#description").fill(description);
  await page.locator("#bannerUrl").fill("https://example.com/banner.png");
  await page.locator('form button[type="submit"]').click();

  await expect(page).toHaveURL(/\/games\/\d+$/);
  await expect(page.locator("h1.page-title")).toContainText(gameTitle);
  await expect(page.getByText(description)).toBeVisible();

  await page.goto("/games");
  await expect(page.getByRole("link").filter({ hasText: gameTitle })).toBeVisible();
});
