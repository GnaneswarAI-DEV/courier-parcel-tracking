package com.courier.tracking;

import com.courier.tracking.dto.UpdateStatusRequest;
import com.courier.tracking.exception.InvalidStatusTransitionException;
import com.courier.tracking.model.Parcel;
import com.courier.tracking.model.ParcelStatus;
import com.courier.tracking.repository.ParcelRepository;
import com.courier.tracking.service.ParcelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ParcelServiceTest {

    @Autowired
    private ParcelService parcelService;

    @Autowired
    private ParcelRepository parcelRepository;

    @Test
    void shouldRejectInvalidStatusTransition() {
        Optional<Parcel> parcel = parcelRepository.findById("CPT2026000004");

        assertTrue(parcel.isPresent());
        assertEquals(ParcelStatus.PICKED_UP, parcel.get().getStatus());

        assertThrows(InvalidStatusTransitionException.class, () ->
            parcelService.updateStatus(
                "CPT2026000004",
                new UpdateStatusRequest(ParcelStatus.DELIVERED, "Pune", "Invalid direct delivery")
            )
        );
    }
}
