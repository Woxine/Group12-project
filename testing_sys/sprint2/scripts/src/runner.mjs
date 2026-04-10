import { nowTimestamp } from './time.mjs';

function matchesGrep(test, grep) {
  if (!grep) return true;
  const g = String(grep).toLowerCase();
  const hay = `${test.name} ${(test.tags ?? []).join(' ')}`.toLowerCase();
  return hay.includes(g);
}

export async function runSuite(suite, { grep, logger }) {
  const startedAt = Date.now();
  const tests = suite.tests.filter((t) => matchesGrep(t, grep));
  const statusByName = new Map();

  let passed = 0;
  let failed = 0;
  let skipped = 0;

  for (const t of tests) {
    const caseStartedAt = Date.now();
    const deps = t.dependsOn ?? [];
    const blockedBy = deps.find((d) => statusByName.get(d) !== 'passed');
    if (blockedBy) {
      skipped++;
      logger.consoleLine(`SKIP ${t.name}\n  blocked by: ${blockedBy}`);
      statusByName.set(t.name, 'skipped');
      await logger.caseResult({
        ts: nowTimestamp(),
        name: t.name,
        tags: t.tags ?? [],
        status: 'skipped',
        durationMs: Date.now() - caseStartedAt,
        error: { message: `blocked by dependency: ${blockedBy}` }
      });
      continue;
    }

    let status = 'passed';
    let error = null;

    try {
      await t.run();
      passed++;
      logger.consoleLine(`PASS ${t.name}`);
    } catch (e) {
      status = 'failed';
      failed++;
      error = e instanceof Error ? { message: e.message, stack: e.stack } : { message: String(e) };
      logger.consoleLine(`FAIL ${t.name}\n  ${error.message}`);
    }

    statusByName.set(t.name, status);
    const durationMs = Date.now() - caseStartedAt;
    await logger.caseResult({
      ts: nowTimestamp(),
      name: t.name,
      tags: t.tags ?? [],
      status,
      durationMs,
      error
    });
  }

  const durationMs = Date.now() - startedAt;
  const summary = {
    total: tests.length,
    passed,
    failed,
    skipped,
    durationMs
  };

  return { summary };
}

