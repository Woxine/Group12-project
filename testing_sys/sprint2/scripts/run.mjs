import { mkdir, writeFile } from 'node:fs/promises';
import { resolve } from 'node:path';
import { parseArgs } from './src/cli.mjs';
import { createLogger } from './src/logger.mjs';
import { createHttpClient } from './src/http.mjs';
import { runSuite } from './src/runner.mjs';
import { buildSprint2Suite } from './src/suites/sprint2.mjs';
import { nowTimestamp } from './src/time.mjs';

async function main() {
  const args = parseArgs(process.argv.slice(2));
  const baseUrl = args.baseUrl ?? 'http://localhost:8080';

  const defaultReportDir = resolve(process.cwd(), 'reports', nowTimestamp());
  const reportDir = resolve(process.cwd(), args.reportDir ?? defaultReportDir);

  await mkdir(reportDir, { recursive: true });

  const httpLogPath = resolve(reportDir, 'http.log');
  const casesPath = resolve(reportDir, 'cases.jsonl');
  const summaryPath = resolve(reportDir, 'summary.json');

  const logger = createLogger({
    verbose: Boolean(args.verbose),
    httpLogPath,
    casesPath
  });

  const http = createHttpClient({
    baseUrl,
    timeoutMs: args.timeoutMs ?? 15_000,
    logger
  });

  const suite = buildSprint2Suite({ http, logger });
  const result = await runSuite(suite, { grep: args.grep, logger });

  await writeFile(summaryPath, JSON.stringify(result.summary, null, 2), 'utf8');
  logger.consoleLine(`\nReport: ${reportDir}`);
  logger.consoleLine(
    `Summary: ${result.summary.passed}/${result.summary.total} passed, ${result.summary.failed} failed, ${result.summary.durationMs}ms`
  );

  if (result.summary.failed > 0) {
    process.exitCode = 1;
  }
}

main().catch((err) => {
  // eslint-disable-next-line no-console
  console.error(err);
  process.exitCode = 1;
});

