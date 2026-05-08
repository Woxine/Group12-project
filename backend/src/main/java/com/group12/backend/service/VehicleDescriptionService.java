package com.group12.backend.service;

import com.group12.backend.dto.VehicleDescriptionRequest;
import com.group12.backend.entity.VehicleDescription;

import java.util.List;

public interface VehicleDescriptionService {
    List<VehicleDescription> getAllDescriptions();
    VehicleDescription getDescriptionByType(String type);
    VehicleDescription updateDescription(String type, VehicleDescriptionRequest request);
}
