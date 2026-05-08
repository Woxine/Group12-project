package com.group12.backend.service.impl;

import com.group12.backend.dto.VehicleDescriptionRequest;
import com.group12.backend.entity.VehicleDescription;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.repository.VehicleDescriptionRepository;
import com.group12.backend.service.VehicleDescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleDescriptionServiceImpl implements VehicleDescriptionService {

    @Autowired
    private VehicleDescriptionRepository repository;

    @Override
    public List<VehicleDescription> getAllDescriptions() {
        return repository.findAllByOrderByVehicleTypeAsc();
    }

    @Override
    public VehicleDescription getDescriptionByType(String type) {
        return repository.findByVehicleType(type)
                .orElseThrow(() -> new BusinessException("Vehicle type not found: " + type, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public VehicleDescription updateDescription(String type, VehicleDescriptionRequest request) {
        VehicleDescription entity = repository.findByVehicleType(type).orElseGet(() -> {
            VehicleDescription vd = new VehicleDescription();
            vd.setVehicleType(type);
            return vd;
        });

        if (request.getDisplay_name() != null) entity.setDisplayName(request.getDisplay_name());
        if (request.getSubtitle() != null) entity.setSubtitle(request.getSubtitle());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getRange_text() != null) entity.setRangeText(request.getRange_text());
        if (request.getSpeed_text() != null) entity.setSpeedText(request.getSpeed_text());
        if (request.getMotor_text() != null) entity.setMotorText(request.getMotor_text());
        if (request.getAdvice() != null) entity.setAdvice(request.getAdvice());

        return repository.save(entity);
    }
}
