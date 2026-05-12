import { test, expect, type Page } from "@playwright/test";
import AxeBuilder from "@axe-core/playwright";

/** 模拟计费设置接口，让可访问性测试不依赖真实后端 / Mock billing APIs so accessibility tests do not depend on the real backend. */
async function mockBillingApis(page: Page) {
  await page.route("**/api/v1/admin/billing-settings**", async (route) => {
    const url = new URL(route.request().url());
    const path = url.pathname;
    const method = route.request().method();

    if (path === "/api/v1/admin/billing-settings" && method === "GET") {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          data: {
            longRentThresholdHours: 24,
            extraLongRentThresholdHours: 72,
            longRentHourRateMultiplier: 0.85,
            extraLongRentHourRateMultiplier: 0.75,
            studentDiscountRate: 0.8,
            seniorDiscountRate: 0.78,
            frequentDiscountRate: 0.76,
            updatedAt: "2026-04-26T20:00:00"
          }
        })
      });
      return;
    }

    if (path === "/api/v1/admin/billing-settings" && method === "PUT") {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          data: {
            longRentThresholdHours: 24,
            extraLongRentThresholdHours: 72,
            longRentHourRateMultiplier: 0.85,
            extraLongRentHourRateMultiplier: 0.75,
            studentDiscountRate: 0.8,
            seniorDiscountRate: 0.78,
            frequentDiscountRate: 0.76,
            updatedAt: "2026-04-26T20:05:00"
          }
        })
      });
      return;
    }

    if (path === "/api/v1/admin/billing-settings/logs") {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          data: [
            {
              id: 1,
              oldLongRentHourRateMultiplier: 0.88,
              newLongRentHourRateMultiplier: 0.85,
              oldExtraLongRentHourRateMultiplier: 0.78,
              newExtraLongRentHourRateMultiplier: 0.75,
              operatorUserId: 1,
              createdAt: "2026-04-26T20:00:00"
            }
          ],
          total: 1
        })
      });
      return;
    }

    await route.fallback();
  });
}

/** 计费页可访问性回归 / Billing page accessibility regression tests. */
test.describe("Billing view accessibility", () => {
  test.beforeEach(async ({ page }) => {
    /** 注入管理员会话，直接进入受保护路由 / Inject an admin session before visiting protected routes. */
    await page.addInitScript(() => {
      localStorage.setItem("admin_token", "test-token");
      localStorage.setItem("admin_role", "ADMIN");
      localStorage.setItem("admin_name", "A11y Admin");
      localStorage.setItem("admin_user_id", "1");
    });
    await mockBillingApis(page);
  });

  /** 使用 axe 检查严重和关键级别问题 / Use axe to check serious and critical violations. */
  test("has no serious or critical axe violations", async ({ page }) => {
    await page.goto("/admin/billing");
    await expect(page.getByText("Billing Discount Hub")).toBeVisible();

    const results = await new AxeBuilder({ page }).analyze();
    const severeViolations = results.violations.filter(
      (violation) => violation.impact === "serious" || violation.impact === "critical"
    );
    expect(severeViolations).toEqual([]);
  });

  /** 确认折扣总览卡片能打开对应编辑弹窗 / Ensure overview cards open the matching adjustment dialog. */
  test("opens dedicated adjustment dialog from overview cards", async ({ page }) => {
    await page.goto("/admin/billing");
    await page.getByText("Student Discount").click();
    await expect(page.getByRole("dialog", { name: "Student Discount Adjustment" })).toBeVisible();
  });
});
