package com.courier.tracking.repository;

import com.courier.tracking.model.Parcel;
import com.courier.tracking.model.ParcelStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParcelRepository extends JpaRepository<Parcel, String> {

    Optional<Parcel> findByTrackingNumber(String trackingNumber);

    Page<Parcel> findByStatus(ParcelStatus status, Pageable pageable);

    Page<Parcel> findByTrackingNumberContainingIgnoreCase(String trackingNumber, Pageable pageable);

    @Query("""
        SELECT p FROM Parcel p
        WHERE p.sender.userId = :customerId
           OR p.receiver.userId = :customerId
        """)
    Page<Parcel> findCustomerParcels(@Param("customerId") Long customerId, Pageable pageable);

    long countByStatus(ParcelStatus status);
}
