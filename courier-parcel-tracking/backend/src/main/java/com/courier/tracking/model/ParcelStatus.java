package com.courier.tracking.model;

public enum ParcelStatus {
    MANIFEST_CREATED,
    PICKED_UP,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    EXCEPTION;

    public boolean canTransitionTo(ParcelStatus next) {
        return switch (this) {
            case MANIFEST_CREATED -> next == PICKED_UP || next == EXCEPTION;
            case PICKED_UP -> next == IN_TRANSIT || next == EXCEPTION;
            case IN_TRANSIT -> next == OUT_FOR_DELIVERY || next == EXCEPTION;
            case OUT_FOR_DELIVERY -> next == DELIVERED || next == EXCEPTION;
            case DELIVERED, EXCEPTION -> false;
        };
    }
}
