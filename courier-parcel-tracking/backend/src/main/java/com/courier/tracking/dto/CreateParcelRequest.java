package com.courier.tracking.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateParcelRequest(
    @NotNull Long senderId,
    @NotNull Long receiverId,
    @NotNull @DecimalMin("0.01") @Digits(integer = 6, fraction = 2) BigDecimal weight,
    @NotBlank @Size(max = 255) String pickupAddress,
    @NotBlank @Size(max = 255) String deliveryAddress,
    LocalDate estimatedDelivery
) {}
