package com.courier.tracking.dto;

import com.courier.tracking.model.Parcel;
import com.courier.tracking.model.ParcelStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ParcelResponse(
    String trackingNumber,
    Long senderId,
    String senderName,
    Long receiverId,
    String receiverName,
    BigDecimal weight,
    String pickupAddress,
    String deliveryAddress,
    ParcelStatus status,
    LocalDate estimatedDelivery,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ParcelResponse from(Parcel p) {
        return new ParcelResponse(
            p.getTrackingNumber(),
            p.getSender().getUserId(),
            p.getSender().getName(),
            p.getReceiver().getUserId(),
            p.getReceiver().getName(),
            p.getWeight(),
            p.getPickupAddress(),
            p.getDeliveryAddress(),
            p.getStatus(),
            p.getEstimatedDelivery(),
            p.getCreatedAt(),
            p.getUpdatedAt()
        );
    }
}
