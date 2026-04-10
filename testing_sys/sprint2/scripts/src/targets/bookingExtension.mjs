export function buildBookingExtensionApi(http) {
  return {
    async extend({ token, bookingId, body }) {
      const { res, body: resp } = await http.patch(`/api/v1/bookings/${encodeURIComponent(bookingId)}/extend`, {
        token,
        json: body
      });
      return { status: res.status, data: resp?.data ?? resp, body: resp };
    }
  };
}

