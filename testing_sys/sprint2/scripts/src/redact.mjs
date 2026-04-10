const TOKEN_RE = /(Bearer\s+)([A-Za-z0-9\-\._~\+\/]+=*)/gi;
const CARD_RE = /\b(\d{9,15})(\d{4})\b/g; // keep last4 only for long digit sequences

export function redactText(input) {
  if (input == null) return '';
  const text = String(input);
  return text
    .replace(TOKEN_RE, (_, prefix) => `${prefix}[REDACTED]`)
    .replace(CARD_RE, (_, __, last4) => `[REDACTED_CARD_****${last4}]`);
}

export function safeJson(value) {
  try {
    return JSON.stringify(value);
  } catch {
    return '"[unserializable]"';
  }
}

