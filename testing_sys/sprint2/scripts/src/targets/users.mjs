export function buildUserApi(http) {
  return {
    async register({ email, password, name }) {
      const { res, body } = await http.post('/api/v1/users', {
        json: { email, password, name }
      });
      return { status: res.status, data: body?.data ?? body, body };
    }
  };
}

