export function buildPaymentCardApi(http) {
  return {
    async list({ userId, token }) {
      const { res, body } = await http.get(`/api/v1/users/${encodeURIComponent(userId)}/payment-cards`, { token });
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
    async listGuest({ userId, guestId, token }) {
      const { res, body } = await http.get(
        `/api/v1/users/${encodeURIComponent(userId)}/payment-cards/guest/${encodeURIComponent(guestId)}`,
        { token }
      );
      return { status: res.status, data: body?.data ?? body, body };
    },
    async bindGuest({ userId, guestId, token, body }) {
      const { res, body: resp } = await http.post(
        `/api/v1/users/${encodeURIComponent(userId)}/payment-cards/guest/${encodeURIComponent(guestId)}`,
        { token, json: body }
      );
      return { status: res.status, data: resp?.data ?? resp, body: resp };
    }
  };
}
