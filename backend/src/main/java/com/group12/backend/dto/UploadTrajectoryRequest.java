package com.group12.backend.dto;

import java.util.List;

public class UploadTrajectoryRequest {
    private List<TrajectoryPointRequest> points;

    public UploadTrajectoryRequest() {}

    public List<TrajectoryPointRequest> getPoints() { return points; }
    public void setPoints(List<TrajectoryPointRequest> points) { this.points = points; }
}
