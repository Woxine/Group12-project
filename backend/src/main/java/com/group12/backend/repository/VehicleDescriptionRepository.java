package com.group12.backend.repository;

import com.group12.backend.entity.VehicleDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleDescriptionRepository extends JpaRepository<VehicleDescription, Long> {
    Optional<VehicleDescription> findByVehicleType(String vehicleType);
    List<VehicleDescription> findAllByOrderByVehicleTypeAsc();
}
