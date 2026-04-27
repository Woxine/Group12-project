import { test, expect, type Page } from "@playwright/test";
import AxeBuilder from "@axe-core/playwright";

async function mockRevenueApis(page: Page) {
  await page.route("**/api/v1/admin/revenue**", async (route) => {
    const url = new URL(route.request().url());
    const path = url.pathname;

    if (path === "/api/v1/admin/revenue") {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          data: {
            totalRevenue: 1265.5,
            totalOrders: 48,
            averageOrderValue: 26.36
          }
        })
      });
      return;
    }

    if (path === "/api/v1/admin/revenue/duration") {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          data: [
            { durationType: "HOURLY", totalOrders: 20, totalRevenue: 450.0 },
            { durationType: "DAILY", totalOrders: 16, totalRevenue: 615.5 }
          ]
        })
      });
      return;
    }

    if (path === "/api/v1/admin/revenue/duration-week") {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          data: [
            { durationType: "HOURLY", totalOrders: 24, totalRevenue: 520.0 },
            { durationType: "DAILY", totalOrders: 18, totalRevenue: 745.5 }
          ]
        })
      });
      return;
    }

    if (path === "/api/v1/admin/revenue/popular-dates-week" || path === "/api/v1/admin/revenue/popular-dates") {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          data: [
            { rank: 1, date: "2026-04-20", orderCount: 12, revenue: 420.0 },
            { rank: 2, date: "2026-04-21", orderCount: 10, revenue: 360.0 }
          ]
        })
      });
      return;
    }

    await route.fallback();
  });
}

test.describe("Revenue view accessibility", () => {
  test.beforeEach(async ({ page }) => {
    await page.addInitScript(() => {
      localStorage.setItem("admin_token", "test-token");
      localStorage.setItem("admin_role", "ADMIN");
      localStorage.setItem("admin_name", "A11y Admin");
      localStorage.setItem("admin_user_id", "1");
    });
    await mockRevenueApis(page);
  });

  test("has no serious or critical axe violations", async ({ page }) => {
    await page.goto("/admin/revenue");
    await expect(page.getByRole("heading", { name: "Revenue Overview" })).toBeVisible();

    const results = await new AxeBuilder({ page }).analyze();
    const severeViolations = results.violations.filter(
      (violation) => violation.impact === "serious" || violation.impact === "critical"
    );
    expect(severeViolations).toEqual([]);
  });

  test("keeps date range and search controls keyboard reachable", async ({ page }) => {
    await page.goto("/admin/revenue");

    await page.keyboard.press("Tab");
    await expect(page.locator(".skip-link")).toBeFocused();

    let foundSearchButton = false;
    for (let i = 0; i < 35; i += 1) {
      await page.keyboard.press("Tab");
      const focusedText = await page.evaluate(() => document.activeElement?.textContent ?? "");
      if (focusedText.includes("Search")) {
        foundSearchButton = true;
        break;
      }
    }
    expect(foundSearchButton).toBeTruthy();
  });
});
