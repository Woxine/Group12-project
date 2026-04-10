import { appendFile, writeFile } from 'node:fs/promises';
import { redactText, safeJson } from './redact.mjs';

export function createLogger({ verbose, httpLogPath, casesPath }) {
  let initialized = false;

  async function ensureInit() {
    if (initialized) return;
    initialized = true;
    await writeFile(httpLogPath, '', 'utf8');
    await writeFile(casesPath, '', 'utf8');
  }

  async function httpLine(line) {
    await ensureInit();
    await appendFile(httpLogPath, redactText(line) + '\n', 'utf8');
  }

  async function caseResult(obj) {
    await ensureInit();
    await appendFile(casesPath, safeJson(obj) + '\n', 'utf8');
  }

  function consoleLine(line) {
    // eslint-disable-next-line no-console
    console.log(line);
  }

  return {
    verbose,
    httpLine,
    caseResult,
    consoleLine
  };
}

