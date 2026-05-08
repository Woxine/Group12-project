package com.group12.backend.dto;

public class TrajectoryPointResponse {
    private Double lat;
    private Double lng;
    private String recordedAt;
    private Integer seq;

    public TrajectoryPointResponse(Double lat, Double lng, String recordedAt, Integer seq) {
        this.lat = lat;
        this.lng = lng;
        this.recordedAt = recordedAt;
        this.seq = seq;
    }

    public Double getLat() { return lat; }
    public Double getLng() { return lng; }
    public String getRecordedAt() { return recordedAt; }
    public Integer getSeq() { return seq; }
}
