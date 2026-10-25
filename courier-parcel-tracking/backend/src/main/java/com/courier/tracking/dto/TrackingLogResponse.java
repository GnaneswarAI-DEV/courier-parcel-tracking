package com.courier.tracking.dto;

import com.courier.tracking.model.ParcelStatus;
import com.courier.tracking.model.TrackingLog;
import java.time.LocalDateTime;

public record TrackingLogResponse(
    Long logId,
    String trackingNumber,
    ParcelStatus status,
    String location,
    String remarks,
    LocalDateTime updatedAt
) {
    public static TrackingLogResponse from(TrackingLog log) {
        return new TrackingLogResponse(
            log.getLogId(),
            log.getParcel().getTrackingNumber(),
            log.getStatus(),
            log.getLocation(),
            log.getRemarks(),
            log.getUpdatedAt()
        );
    }
}
