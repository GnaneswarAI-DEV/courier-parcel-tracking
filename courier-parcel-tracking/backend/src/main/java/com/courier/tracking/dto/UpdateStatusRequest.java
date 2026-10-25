package com.courier.tracking.dto;

import com.courier.tracking.model.ParcelStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateStatusRequest(
    @NotNull ParcelStatus status,
    @NotBlank @Size(max = 150) String location,
    @Size(max = 500) String remarks
) {}
