export function buildFeedbackApi(http) {
  return {
    async submit({ token, body }) {
      const { res, body: resp } = await http.post('/api/v1/feedbacks', { token, json: body });
      return { status: res.status, data: resp?.data ?? resp, body: resp };
    },
    async processPriority({ token, feedbackId, body }) {
      const { res, body: resp } = await http.put(
        `/api/v1/feedbacks/${encodeURIComponent(feedbackId)}/process-priority`,
        { token, json: body }
      );
      return { status: res.status, data: resp?.data ?? resp, body: resp };
    },
    async listHighPriority({ token, escalated, page, size }) {
      const q = new URLSearchParams();
      if (escalated != null) q.set('escalated', String(escalated));
      if (page != null) q.set('page', String(page));
      if (size != null) q.set('size', String(size));
      const suffix = q.toString() ? `?${q.toString()}` : '';
      const { res, body } = await http.get(`/api/v1/feedbacks/high-priority${suffix}`, { token });
      return { status: res.status, data: body?.data ?? body, body };
    }
  };
}
