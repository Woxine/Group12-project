package com.group12.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group12.backend.entity.LocationPoint;

@Repository
public interface LocationPointRepository extends JpaRepository<LocationPoint, Long> {
}
