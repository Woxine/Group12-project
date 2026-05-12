import { defineConfig, devices } from "@playwright/test";

/** Playwright 配置，专门运行前端可访问性回归测试 / Playwright config dedicated to frontend accessibility regression tests. */
export default defineConfig({
  testDir: "./src/tests/accessibility",
  fullyParallel: true,
  timeout: 45_000,
  expect: {
    timeout: 10_000
  },
  /** 测试统一访问 Vite dev server，并在失败重试时保留 trace / Tests use the Vite dev server and keep traces on first retry. */
  use: {
    baseURL: "http://127.0.0.1:4173",
    trace: "on-first-retry"
  },
  /** 测试前自动启动或复用本地前端服务 / Start or reuse the local frontend server before tests. */
  webServer: {
    command: "npm run dev -- --host 127.0.0.1 --port 4173",
    url: "http://127.0.0.1:4173",
    reuseExistingServer: true,
    timeout: 120_000
  },
  /** 当前只覆盖 Chromium 桌面环境 / Currently cover the desktop Chromium environment. */
  projects: [
    {
      name: "chromium",
      use: { ...devices["Desktop Chrome"] }
    }
  ]
});
