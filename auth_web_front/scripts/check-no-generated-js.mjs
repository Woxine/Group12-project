import { existsSync, readdirSync } from "node:fs";
import { join, relative } from "node:path";

const rootDir = process.cwd();
const srcDir = join(rootDir, "src");
const blockedFiles = [];

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

if (!existsSync(srcDir)) {
  console.error("[guard:no-generated-js] src directory not found.");
  process.exit(1);
}

walk(srcDir);

if (blockedFiles.length > 0) {
  console.error("[guard:no-generated-js] Generated .js files detected under src/:");
  for (const file of blockedFiles) {
    console.error(` - ${file}`);
  }
  console.error("Please remove these files and keep source files as .ts/.vue only.");
  process.exit(1);
}

console.log("[guard:no-generated-js] OK");
