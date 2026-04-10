import { assertEq } from '../assert.mjs';

export function buildAuthApi(http) {
  return {
    async login({ email, password }) {
      const { res, body } = await http.post('/api/v1/auth/login', {
        json: { email, password }
      });
      assertEq(res.status, 200);
      return body?.data ?? body;
    }
  };
}

