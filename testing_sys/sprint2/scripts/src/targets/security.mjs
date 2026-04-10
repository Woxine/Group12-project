export function buildSecurityApi(http) {
  return {
    async getSettings({ token }) {
      const { res, body } = await http.get('/api/v1/security/settings', { token });
      return { status: res.status, data: body?.data ?? body, body };
    },
    async updateSettings({ token, body }) {
      const { res, body: resp } = await http.post('/api/v1/security/settings', { token, json: body });
      return { status: res.status, data: resp?.data ?? resp, body: resp };
    }
  };
}

