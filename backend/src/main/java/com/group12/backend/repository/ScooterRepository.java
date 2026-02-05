package com.group12.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group12.backend.entity.Scooter;

@Repository
public interface ScooterRepository extends JpaRepository<Scooter, Long> {
    List<Scooter> findByStatus(String status);
}
