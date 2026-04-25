import { mkdir, writeFile } from 'node:fs/promises';
import { resolve } from 'node:path';

import { parseArgs } from '../../sprint2/scripts/src/cli.mjs';
import { createLogger } from '../../sprint2/scripts/src/logger.mjs';
import { createHttpClient } from '../../sprint2/scripts/src/http.mjs';
import { runSuite } from '../../sprint2/scripts/src/runner.mjs';
import { nowTimestamp } from '../../sprint2/scripts/src/time.mjs';

import { buildSprint3State1Suite } from './src/suites/state1.mjs';
import { buildSprint3State2Suite } from './src/suites/state2.mjs';

function mergeSuites(suites) {
  const tests = suites.flatMap((s) => s.tests ?? []);
  return { tests };
}

async function main() {
  const args = parseArgs(process.argv.slice(2));
  const baseUrl = args.baseUrl ?? 'http://localhost:8080';
  const suiteName = (args.suite ?? 'all').toLowerCase();

  const defaultReportDir = resolve(process.cwd(), 'reports', nowTimestamp());
  const reportDir = resolve(process.cwd(), args.reportDir ?? defaultReportDir);
  await mkdir(reportDir, { recursive: true });

  const logger = createLogger({
    verbose: Boolean(args.verbose),
    httpLogPath: resolve(reportDir, 'http.log'),
    casesPath: resolve(reportDir, 'cases.jsonl')
  });
  const http = createHttpClient({
    baseUrl,
    timeoutMs: args.timeoutMs ?? 20_000,
    logger
  });

  const state1 = buildSprint3State1Suite({ http, logger });
  const state2 = buildSprint3State2Suite({ http, logger });

  let suite;
  if (suiteName === 'state1') suite = state1;
  else if (suiteName === 'state2') suite = state2;
  else suite = mergeSuites([state1, state2]);

  const result = await runSuite(suite, { grep: args.grep, logger });
  await writeFile(resolve(reportDir, 'summary.json'), JSON.stringify(result.summary, null, 2), 'utf8');

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
