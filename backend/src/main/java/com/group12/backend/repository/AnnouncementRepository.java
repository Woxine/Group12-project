package com.group12.backend.repository;

import com.group12.backend.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query("SELECT a FROM Announcement a WHERE a.enabled = true " +
           "AND (a.startTime IS NULL OR a.startTime <= :now) " +
           "AND (a.endTime IS NULL OR a.endTime >= :now) " +
           "ORDER BY a.createdAt DESC")
    List<Announcement> findActiveAnnouncements(@Param("now") LocalDateTime now);
}
