export function buildPaymentCardApi(http) {
  return {
    async list({ userId, token }) {
      const { res, body } = await http.get(`/api/v1/users/${encodeURIComponent(userId)}/payment-cards`, {
        token
      });
      return { status: res.status, data: body?.data ?? body, body };
    },
    async create({ userId, token, body }) {
      const { res, body: resp } = await http.post(`/api/v1/users/${encodeURIComponent(userId)}/payment-cards`, {
        token,
        json: body
      });
      return { status: res.status, data: resp?.data ?? resp, body: resp };
    },
    async setDefault({ userId, cardId, token }) {
      const { res, body } = await http.post(
        `/api/v1/users/${encodeURIComponent(userId)}/payment-cards/${encodeURIComponent(cardId)}/default`,
        { token }
      );
      return { status: res.status, data: body?.data ?? body, body };
    },
    async del({ userId, cardId, token }) {
      const { res, body } = await http.del(
        `/api/v1/users/${encodeURIComponent(userId)}/payment-cards/${encodeURIComponent(cardId)}`,
        { token }
      );
      return { status: res.status, data: body?.data ?? body, body };
    }
  };
}

