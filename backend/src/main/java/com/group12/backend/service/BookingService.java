package com.group12.backend.service;

import com.group12.backend.dto.CreateBookingRequest;

public interface BookingService {
    Object createBooking(CreateBookingRequest request);
    Object cancelBooking(String bookingId, Double endLat, Double endLng);
    Object completeBooking(String bookingId, Double endLat, Double endLng);
}
