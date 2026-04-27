package com.group12.backend.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group12.backend.entity.Scooter;

@Repository
public interface ScooterRepository extends JpaRepository<Scooter, Long> {
    List<Scooter> findByStatus(String status);
    List<Scooter> findByType(String type);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Scooter s WHERE s.id = :id")
    Optional<Scooter> findByIdForUpdate(@Param("id") Long id);
}
