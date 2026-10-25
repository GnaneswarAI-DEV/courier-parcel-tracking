package com.courier.tracking.repository;

import com.courier.tracking.model.TrackingLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingLogRepository extends JpaRepository<TrackingLog, Long> {
    Page<TrackingLog> findByParcelTrackingNumberOrderByUpdatedAtAsc(
        String trackingNumber,
        Pageable pageable
    );
}
