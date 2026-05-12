import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "node:path";

/** Vite 构建配置，注册 Vue 插件和 @ 到 src 的别名 / Vite build config registering Vue and the @ alias to src. */
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src")
    }
  },
  /** 固定开发端口，便于本地调试和测试配置复用 / Fixed dev port for local debugging and test reuse. */
  server: {
    port: 5173
  }
});
