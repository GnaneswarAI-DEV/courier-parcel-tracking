package com.courier.tracking.dto;

import com.courier.tracking.model.UserRole;

public record AuthResponse(
    String token,
    Long userId,
    String name,
    String email,
    UserRole role
) {}
