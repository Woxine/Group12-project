export function buildAdminApi(http) {
  return {
    async listScooters({ token, status, page, size, limit } = {}) {
      const q = new URLSearchParams();
      if (status != null) q.set('status', status);
      if (page != null) q.set('page', String(page));
      if (size != null) q.set('size', String(size));
      if (limit != null) q.set('limit', String(limit));
      const suffix = q.toString() ? `?${q.toString()}` : '';
      const { res, body } = await http.get(`/api/v1/admin/scooters${suffix}`, { token });
      return { status: res.status, data: body?.data ?? body, body };
    },
    async createScooter({ token, body }) {
      const { res, body: resp } = await http.post('/api/v1/admin/scooters', { token, json: body });
      return { status: res.status, data: resp?.data ?? resp, body: resp };
    },
    async popularDatesWeek({ token }) {
      const { res, body } = await http.get('/api/v1/admin/revenue/popular-dates-week', { token });
      return { status: res.status, data: body?.data ?? body, body };
    }
  };
}
