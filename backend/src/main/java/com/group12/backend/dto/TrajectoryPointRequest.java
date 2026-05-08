package com.group12.backend.dto;

public class TrajectoryPointRequest {
    private Double lat;
    private Double lng;
    private String recordedAt;
    private Integer seq;

    public TrajectoryPointRequest() {}

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }

    public String getRecordedAt() { return recordedAt; }
    public void setRecordedAt(String recordedAt) { this.recordedAt = recordedAt; }

    public Integer getSeq() { return seq; }
    public void setSeq(Integer seq) { this.seq = seq; }
}
