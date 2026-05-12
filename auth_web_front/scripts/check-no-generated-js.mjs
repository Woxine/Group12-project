import { existsSync, readdirSync } from "node:fs";
import { join, relative } from "node:path";

/** 仓库根目录和前端源码目录 / Repository root and frontend source directory. */
const rootDir = process.cwd();
const srcDir = join(rootDir, "src");
const blockedFiles = [];

/** 递归扫描 src，阻止生成的 .js 文件混入源码 / Recursively scan src and block generated .js files from source. */
function walk(dirPath) {
  const entries = readdirSync(dirPath, { withFileTypes: true });
  for (const entry of entries) {
    const fullPath = join(dirPath, entry.name);
    if (entry.isDirectory()) {
      walk(fullPath);
      continue;
    }
    if (!entry.isFile()) {
      continue;
    }
    if (entry.name.endsWith(".js")) {
      blockedFiles.push(relative(rootDir, fullPath));
    }
  }
}

/** 缺少 src 时说明命令运行目录不正确 / Missing src means the command ran from the wrong directory. */
if (!existsSync(srcDir)) {
  console.error("[guard:no-generated-js] src directory not found.");
  process.exit(1);
}

walk(srcDir);

/** 发现 .js 源文件时中断开发或构建流程 / Stop dev or build flow when .js source files are found. */
if (blockedFiles.length > 0) {
  console.error("[guard:no-generated-js] Generated .js files detected under src/:");
  for (const file of blockedFiles) {
    console.error(` - ${file}`);
  }
  console.error("Please remove these files and keep source files as .ts/.vue only.");
  process.exit(1);
}

console.log("[guard:no-generated-js] OK");
