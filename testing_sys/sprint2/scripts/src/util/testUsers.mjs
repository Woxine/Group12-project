function randSuffix() {
  const d = new Date();
  const r = Math.random().toString(16).slice(2, 8);
  return `${d.getFullYear()}${String(d.getMonth() + 1).padStart(2, '0')}${String(d.getDate()).padStart(2, '0')}-${r}`;
}

export function makeTestUsers() {
  const suffix = randSuffix();
  const userA = {
    email: `sys_a_${suffix}@example.com`,
    password: 'Passw0rd!234',
    name: `Sys A ${suffix}`
  };
  const userB = {
    email: `sys_b_${suffix}@example.com`,
    password: 'Passw0rd!234',
    name: `Sys B ${suffix}`
  };
  return { userA, userB };
}

