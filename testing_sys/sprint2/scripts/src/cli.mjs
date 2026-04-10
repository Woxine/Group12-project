function toNumber(value, { name }) {
  if (value == null) return undefined;
  const n = Number(value);
  if (!Number.isFinite(n)) {
    throw new Error(`Invalid --${name}: ${value}`);
  }
  return n;
}

export function parseArgs(argv) {
  const out = {};
  const positional = [];
  for (let i = 0; i < argv.length; i++) {
    const a = argv[i];
    if (!a.startsWith('--')) {
      positional.push(a);
      continue;
    }
    const key = a.slice(2);
    if (key === 'verbose') {
      out.verbose = true;
      continue;
    }
    const value = argv[i + 1];
    i++;
    if (key === 'timeoutMs') out.timeoutMs = toNumber(value, { name: key });
    else out[key] = value;
  }

  // npm may pass base URL as positional arg in some cases.
  if (!out.baseUrl) {
    const urlLike = positional.find((x) => /^https?:\/\//i.test(x));
    if (urlLike) out.baseUrl = urlLike;
  }
  return out;
}

