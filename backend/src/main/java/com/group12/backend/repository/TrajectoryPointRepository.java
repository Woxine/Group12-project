package com.group12.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group12.backend.entity.TrajectoryPoint;

@Repository
public interface TrajectoryPointRepository extends JpaRepository<TrajectoryPoint, Long> {
    List<TrajectoryPoint> findByBooking_IdOrderBySeqAsc(Long bookingId);

    @Query("SELECT COALESCE(MAX(tp.seq), 0) FROM TrajectoryPoint tp WHERE tp.booking.id = :bookingId")
    Integer findMaxSeqByBookingId(@Param("bookingId") Long bookingId);

    boolean existsByBooking_Id(Long bookingId);
}
