import { setTimeout as delay } from 'node:timers/promises';
import { redactText } from './redact.mjs';

function normalizeBaseUrl(baseUrl) {
  return baseUrl.endsWith('/') ? baseUrl.slice(0, -1) : baseUrl;
}

function withTimeout(promise, timeoutMs, { label }) {
  let timeoutId;
  const timeoutPromise = new Promise((_, reject) => {
    timeoutId = setTimeout(() => reject(new Error(`Timeout after ${timeoutMs}ms: ${label}`)), timeoutMs);
  });
  return Promise.race([promise, timeoutPromise]).finally(() => clearTimeout(timeoutId));
}

async function readBodySafe(res) {
  const text = await res.text();
  try {
    return { kind: 'json', value: JSON.parse(text), raw: text };
  } catch {
    return { kind: 'text', value: text, raw: text };
  }
}

export function createHttpClient({ baseUrl, timeoutMs, logger }) {
  const base = normalizeBaseUrl(baseUrl);

  async function request(method, path, { token, json, headers } = {}) {
    const url = `${base}${path}`;
    const h = {
      ...(headers ?? {}),
      ...(json != null ? { 'Content-Type': 'application/json' } : {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {})
    };

    const body = json != null ? JSON.stringify(json) : undefined;

    await logger.httpLine(`--> ${method} ${url}`);
    if (logger.verbose) {
      await logger.httpLine(`    headers: ${redactText(JSON.stringify(h))}`);
      if (body != null) await logger.httpLine(`    body: ${redactText(body)}`);
    }

    const res = await withTimeout(
      fetch(url, { method, headers: h, body }),
      timeoutMs,
      { label: `${method} ${path}` }
    );

    const bodyInfo = await readBodySafe(res);
    await logger.httpLine(`<-- ${res.status} ${method} ${url}`);
    if (logger.verbose) {
      await logger.httpLine(`    response: ${redactText(bodyInfo.raw)}`);
    }

    return { res, body: bodyInfo.value };
  }

  return {
    get: (path, opts) => request('GET', path, opts),
    post: (path, opts) => request('POST', path, opts),
    patch: (path, opts) => request('PATCH', path, opts),
    put: (path, opts) => request('PUT', path, opts),
    del: (path, opts) => request('DELETE', path, opts),
    sleep: (ms) => delay(ms)
  };
}

