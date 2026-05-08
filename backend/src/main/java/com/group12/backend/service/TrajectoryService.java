package com.group12.backend.service;

import java.util.List;

import com.group12.backend.dto.TrajectoryPointResponse;
import com.group12.backend.dto.UploadTrajectoryRequest;

public interface TrajectoryService {
    Object uploadTrajectory(String bookingId, Long authUserId, UploadTrajectoryRequest request);
    List<TrajectoryPointResponse> getTrajectory(String bookingId, Long authUserId);
}
