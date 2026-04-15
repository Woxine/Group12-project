package com.group12.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group12.backend.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_Id(Long userId);
    Page<Booking> findByUser_IdOrderByStartTimeDesc(Long userId, Pageable pageable);
    List<Booking> findByUser_IdAndStatus(Long userId, String status);
    List<Booking> findByScooterId(Long scooterId);
    List<Booking> findByStatusAndEndTimeBefore(String status, LocalDateTime endTime);

    @Query("SELECT b FROM Booking b WHERE b.scooter.id = :scooterId AND b.status = 'CONFIRMED' " +
           "AND b.endTime > :startTime AND b.startTime < :endTime")
    List<Booking> findOverlappingBookings(@Param("scooterId") Long scooterId,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COALESCE(SUM(b.durationHours), 0) FROM Booking b " +
           "WHERE b.user.id = :userId AND b.status = 'COMPLETED' " +
           "AND b.endTime >= :fromTime AND b.endTime <= :toTime")
    Double sumCompletedDurationHours(@Param("userId") Long userId,
                                     @Param("fromTime") LocalDateTime fromTime,
                                     @Param("toTime") LocalDateTime toTime);
}
