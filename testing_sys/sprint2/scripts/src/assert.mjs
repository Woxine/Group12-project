export function assert(condition, message = 'Assertion failed') {
  if (!condition) throw new Error(message);
}

export function assertEq(actual, expected, message) {
  if (actual !== expected) {
    throw new Error(message ?? `Expected ${JSON.stringify(expected)}, got ${JSON.stringify(actual)}`);
  }
}

export function assertIn(item, list, message) {
  if (!list.includes(item)) {
    throw new Error(message ?? `Expected ${JSON.stringify(item)} in ${JSON.stringify(list)}`);
  }
}

export function assertMatch(text, re, message) {
  if (!re.test(String(text))) {
    throw new Error(message ?? `Expected ${JSON.stringify(text)} to match ${re}`);
  }
}

export function pick(obj, keys) {
  const out = {};
  for (const k of keys) out[k] = obj?.[k];
  return out;
}

