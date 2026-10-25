package com.courier.tracking.controller;

import com.courier.tracking.dto.*;
import com.courier.tracking.model.ParcelStatus;
import com.courier.tracking.service.ParcelService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parcels")
public class ParcelController {

    private final ParcelService parcelService;

    public ParcelController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @PostMapping
    public ResponseEntity<ParcelResponse> createParcel(
        @Valid @RequestBody CreateParcelRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(parcelService.createParcel(request));
    }

    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<ParcelResponse> trackParcel(
        @PathVariable String trackingNumber
    ) {
        return ResponseEntity.ok(parcelService.getTrackingDetails(trackingNumber));
    }

    @GetMapping("/{trackingNumber}/history")
    public ResponseEntity<Page<TrackingLogResponse>> getHistory(
        @PathVariable String trackingNumber,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
            parcelService.getHistory(trackingNumber, PageRequest.of(page, size))
        );
    }

    @PatchMapping("/{trackingNumber}/status")
    public ResponseEntity<ParcelResponse> updateStatus(
        @PathVariable String trackingNumber,
        @Valid @RequestBody UpdateStatusRequest request
    ) {
        return ResponseEntity.ok(parcelService.updateStatus(trackingNumber, request));
    }

    @GetMapping
    public ResponseEntity<Page<ParcelResponse>> listParcels(
        @RequestParam(required = false) ParcelStatus status,
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(
            page, size, Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(parcelService.listParcels(status, search, pageable));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<ParcelResponse>> customerParcels(
        @PathVariable Long customerId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(
            page, size, Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(parcelService.getCustomerParcels(customerId, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> dashboardStats() {
        return ResponseEntity.ok(parcelService.getDashboardStats());
    }
}
