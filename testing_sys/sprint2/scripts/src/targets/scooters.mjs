export function buildScooterApi(http) {
  return {
    async list({ status, page, size, limit } = {}) {
      const q = new URLSearchParams();
      if (status != null) q.set('status', status);
      if (page != null) q.set('page', String(page));
      if (size != null) q.set('size', String(size));
      if (limit != null) q.set('limit', String(limit));
      const suffix = q.toString() ? `?${q.toString()}` : '';
      const { res, body } = await http.get(`/api/v1/scooters${suffix}`);
      return { status: res.status, body };
    }
  };
}

