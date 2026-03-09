package com.group12.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group12.backend.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_Id(Long userId);
    Page<Booking> findByUser_IdOrderByStartTimeDesc(Long userId, Pageable pageable);
    List<Booking> findByUser_IdAndStatus(Long userId, String status);
    List<Booking> findByScooterId(Long scooterId);
    List<Booking> findByStatusAndEndTimeBefore(String status, LocalDateTime endTime);
}
