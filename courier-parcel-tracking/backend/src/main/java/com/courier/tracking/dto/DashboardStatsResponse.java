package com.courier.tracking.dto;

public record DashboardStatsResponse(
    long totalParcels,
    long deliveredParcels,
    long inTransitParcels,
    long exceptionParcels
) {}
