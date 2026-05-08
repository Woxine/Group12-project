package com.group12.backend.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.TrajectoryPointRequest;
import com.group12.backend.dto.TrajectoryPointResponse;
import com.group12.backend.dto.UploadTrajectoryRequest;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.TrajectoryPoint;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.exception.ErrorMessages;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.TrajectoryPointRepository;
import com.group12.backend.service.TrajectoryService;

@Service
public class TrajectoryServiceImpl implements TrajectoryService {
    private final BookingRepository bookingRepository;
    private final TrajectoryPointRepository trajectoryPointRepository;

    public TrajectoryServiceImpl(BookingRepository bookingRepository,
                                  TrajectoryPointRepository trajectoryPointRepository) {
        this.bookingRepository = bookingRepository;
        this.trajectoryPointRepository = trajectoryPointRepository;
    }

    @Override
    public Object uploadTrajectory(String bookingId, Long authUserId, UploadTrajectoryRequest request) {
        Long id = Long.parseLong(bookingId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorMessages.BOOKING_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!booking.getUser().getId().equals(authUserId)) {
            throw new BusinessException(ErrorMessages.FORBIDDEN, HttpStatus.FORBIDDEN);
        }

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new BusinessException("Cannot record trajectory for a booking that is not active", HttpStatus.CONFLICT);
        }

        List<TrajectoryPointRequest> points = request.getPoints();
        if (points == null || points.isEmpty()) {
            return Map.of("saved", 0);
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        List<TrajectoryPoint> entities = new ArrayList<>();
        for (TrajectoryPointRequest pt : points) {
            TrajectoryPoint entity = new TrajectoryPoint();
            entity.setBooking(booking);
            entity.setLat(pt.getLat());
            entity.setLng(pt.getLng());
            entity.setSeq(pt.getSeq() != null ? pt.getSeq() : 0);
            if (pt.getRecordedAt() != null && !pt.getRecordedAt().isBlank()) {
                try {
                    entity.setRecordedAt(LocalDateTime.parse(pt.getRecordedAt(), fmt));
                } catch (Exception e) {
                    entity.setRecordedAt(LocalDateTime.now());
                }
            } else {
                entity.setRecordedAt(LocalDateTime.now());
            }
            entities.add(entity);
        }

        trajectoryPointRepository.saveAll(entities);
        return Map.of("saved", entities.size());
    }

    @Override
    public List<TrajectoryPointResponse> getTrajectory(String bookingId, Long authUserId) {
        Long id = Long.parseLong(bookingId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorMessages.BOOKING_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!booking.getUser().getId().equals(authUserId)) {
            throw new BusinessException(ErrorMessages.FORBIDDEN, HttpStatus.FORBIDDEN);
        }

        List<TrajectoryPoint> points = trajectoryPointRepository.findByBooking_IdOrderBySeqAsc(id);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        List<TrajectoryPointResponse> result = new ArrayList<>();
        for (TrajectoryPoint pt : points) {
            result.add(new TrajectoryPointResponse(
                    pt.getLat(),
                    pt.getLng(),
                    pt.getRecordedAt() != null ? pt.getRecordedAt().format(fmt) : "",
                    pt.getSeq()
            ));
        }
        return result;
    }
}
