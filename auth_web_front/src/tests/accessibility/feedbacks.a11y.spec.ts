import { test, expect, type Page } from "@playwright/test";
import AxeBuilder from "@axe-core/playwright";

async function mockFeedbackApis(page: Page) {
  await page.route("**/api/v1/feedbacks**", async (route) => {
    const url = new URL(route.request().url());
    const method = route.request().method();

    if (method === "GET" && url.pathname === "/api/v1/feedbacks") {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          data: [
            {
              id: 101,
              userId: 1,
              scooterId: 7,
              content: "Brake response feels delayed.",
              priority: "HIGH",
              resolved: false,
              escalated: false,
              escalatedTo: null,
              escalationStatus: "PENDING"
            }
          ],
          total: 1
        })
      });
      return;
    }

    if (method === "PUT" && url.pathname.includes("/process-priority")) {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          data: {
            feedbackId: 101,
            priority: "HIGH",
            escalated: true,
            escalatedTo: "TECH_TEAM",
            status: "ESCALATED"
          }
        })
      });
      return;
    }

    await route.fallback();
  });
}

test.describe("Feedback management accessibility", () => {
  test.beforeEach(async ({ page }) => {
    await page.addInitScript(() => {
      localStorage.setItem("admin_token", "test-token");
      localStorage.setItem("admin_role", "ADMIN");
      localStorage.setItem("admin_name", "A11y Admin");
      localStorage.setItem("admin_user_id", "1");
    });
    await mockFeedbackApis(page);
  });

  test("has no serious or critical axe violations", async ({ page }) => {
    await page.goto("/admin/feedbacks");
    await expect(page.getByRole("heading", { name: "Feedback Management" })).toBeVisible();

    const results = await new AxeBuilder({ page })
      // Element Plus renders an internal combobox input for ElSelect that is not user-facing.
      .exclude(".el-select__input")
      .analyze();
    const severeViolations = results.violations.filter(
      (violation) => violation.impact === "serious" || violation.impact === "critical"
    );
    expect(severeViolations).toEqual([]);
  });

  test("supports keyboard navigation to core controls with visible focus", async ({ page }) => {
    await page.goto("/admin/feedbacks");

    await page.keyboard.press("Tab");
    await expect(page.locator(".skip-link")).toBeFocused();

    let foundSearchButton = false;
    for (let i = 0; i < 30; i += 1) {
      await page.keyboard.press("Tab");
      const focusedText = await page.evaluate(() => document.activeElement?.textContent ?? "");
      if (focusedText.includes("Search")) {
        foundSearchButton = true;
        break;
      }
    }
    expect(foundSearchButton).toBeTruthy();

    const outlineStyle = await page.evaluate(() => {
      const active = document.activeElement;
      return active ? window.getComputedStyle(active).outlineStyle : "none";
    });
    expect(outlineStyle).not.toBe("none");
  });
});
