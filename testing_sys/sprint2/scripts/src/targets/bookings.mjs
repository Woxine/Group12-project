export function buildBookingApi(http) {
  return {
    async create({ token, body }) {
      const { res, body: resp } = await http.post('/api/v1/bookings', { token, json: body });
      return { status: res.status, data: resp?.data ?? resp, body: resp };
    },
    async cancel({ token, bookingId, body }) {
      const { res, body: resp } = await http.del(`/api/v1/bookings/${encodeURIComponent(bookingId)}`, {
        token,
        json: body
      });
      return { status: res.status, data: resp?.data ?? resp, body: resp };
    },
    async complete({ token, bookingId, body }) {
      const { res, body: resp } = await http.patch(`/api/v1/bookings/${encodeURIComponent(bookingId)}/complete`, {
        token,
        json: body
      });
      return { status: res.status, data: resp?.data ?? resp, body: resp };
    }
  };
}

