package com.courier.tracking.service;

import com.courier.tracking.dto.*;
import com.courier.tracking.exception.InvalidStatusTransitionException;
import com.courier.tracking.exception.ResourceNotFoundException;
import com.courier.tracking.model.*;
import com.courier.tracking.repository.ParcelRepository;
import com.courier.tracking.repository.TrackingLogRepository;
import com.courier.tracking.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ParcelService {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final ParcelRepository parcelRepository;
    private final TrackingLogRepository trackingLogRepository;
    private final UserRepository userRepository;
    private final SecureRandom random = new SecureRandom();

    public ParcelService(
        ParcelRepository parcelRepository,
        TrackingLogRepository trackingLogRepository,
        UserRepository userRepository
    ) {
        this.parcelRepository = parcelRepository;
        this.trackingLogRepository = trackingLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ParcelResponse createParcel(CreateParcelRequest request) {
        User sender = userRepository.findById(request.senderId())
            .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        User receiver = userRepository.findById(request.receiverId())
            .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        Parcel parcel = new Parcel();
        parcel.setTrackingNumber(generateTrackingNumber());
        parcel.setSender(sender);
        parcel.setReceiver(receiver);
        parcel.setWeight(request.weight());
        parcel.setPickupAddress(request.pickupAddress());
        parcel.setDeliveryAddress(request.deliveryAddress());
        parcel.setStatus(ParcelStatus.MANIFEST_CREATED);
        parcel.setEstimatedDelivery(request.estimatedDelivery());

        parcelRepository.save(parcel);
        addLog(parcel, ParcelStatus.MANIFEST_CREATED, request.pickupAddress(), "Parcel created");

        return ParcelResponse.from(parcel);
    }

    @Transactional(readOnly = true)
    public ParcelResponse getTrackingDetails(String trackingNumber) {
        return ParcelResponse.from(findParcel(trackingNumber));
    }

    @Transactional(readOnly = true)
    public Page<TrackingLogResponse> getHistory(String trackingNumber, Pageable pageable) {
        Parcel parcel = findParcel(trackingNumber);

        return trackingLogRepository
            .findByParcelTrackingNumberOrderByUpdatedAtAsc(parcel.getTrackingNumber(), pageable)
            .map(TrackingLogResponse::from);
    }

    @Transactional
    public ParcelResponse updateStatus(String trackingNumber, UpdateStatusRequest request) {
        Parcel parcel = findParcel(trackingNumber);
        ParcelStatus next = request.status();

        if (!parcel.getStatus().canTransitionTo(next)) {
            throw new InvalidStatusTransitionException(
                "Invalid status transition from " + parcel.getStatus() + " to " + next
            );
        }

        parcel.setStatus(next);
        addLog(parcel, next, request.location(), request.remarks());

        return ParcelResponse.from(parcel);
    }

    @Transactional(readOnly = true)
    public Page<ParcelResponse> listParcels(
        ParcelStatus status,
        String search,
        Pageable pageable
    ) {
        Page<Parcel> page;

        if (status != null) {
            page = parcelRepository.findByStatus(status, pageable);
        } else if (search != null && !search.isBlank()) {
            page = parcelRepository.findByTrackingNumberContainingIgnoreCase(search, pageable);
        } else {
            page = parcelRepository.findAll(pageable);
        }

        return page.map(ParcelResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<ParcelResponse> getCustomerParcels(Long customerId, Pageable pageable) {
        return parcelRepository.findCustomerParcels(customerId, pageable)
            .map(ParcelResponse::from);
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        long total = parcelRepository.count();
        long delivered = parcelRepository.countByStatus(ParcelStatus.DELIVERED);
        long inTransit = parcelRepository.countByStatus(ParcelStatus.IN_TRANSIT);
        long exceptions = parcelRepository.countByStatus(ParcelStatus.EXCEPTION);

        return new DashboardStatsResponse(total, delivered, inTransit, exceptions);
    }

    private Parcel findParcel(String trackingNumber) {
        return parcelRepository.findByTrackingNumber(trackingNumber)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Parcel not found: " + trackingNumber
            ));
    }

    private void addLog(Parcel parcel, ParcelStatus status, String location, String remarks) {
        TrackingLog log = new TrackingLog();
        log.setParcel(parcel);
        log.setStatus(status);
        log.setLocation(location);
        log.setRemarks(remarks);
        trackingLogRepository.save(log);
    }

    private String generateTrackingNumber() {
        String prefix = "CPT" + LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy"));

        String candidate;
        do {
            StringBuilder suffix = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                suffix.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            }
            candidate = prefix + suffix;
        } while (parcelRepository.existsById(candidate));

        return candidate;
    }
}
