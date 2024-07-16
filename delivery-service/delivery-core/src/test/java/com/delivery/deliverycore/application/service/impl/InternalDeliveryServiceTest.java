package com.delivery.deliverycore.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.delivery.deliverycore.infrastructure.entity.Delivery;
import com.delivery.deliverycore.infrastructure.entity.DeliveryStatus;
import com.delivery.deliverycore.infrastructure.repository.DeliveryRepository;
import com.delivery.deliverycore.testConfig.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class InternalDeliveryServiceTest extends IntegrationTestSupport {
    @Autowired
    private InternalDeliveryService internalDeliveryService;
    @Autowired
    private DeliveryRepository deliveryRepository;

    @BeforeEach
    void setUp() {
        deliveryRepository.deleteAllInBatch();
    }

    @DisplayName("배송 요청 상태 체크 true")
    @Test
    void delivery_status_check_true() {
        // given
        Delivery delivery = Delivery.builder()
            .status(DeliveryStatus.REQUESTED)
            .build();
        deliveryRepository.save(delivery);

        // when
        Boolean result = internalDeliveryService.deliveryStatusCheck(delivery.getId());

        // then
        assertTrue(result);
    }

    @DisplayName("배송 요청 상태 체크 false")
    @Test
    void delivery_status_check_false() {
        // given
        Delivery delivery = Delivery.builder()
            .status(DeliveryStatus.IN_DELIVERY)
            .build();
        deliveryRepository.save(delivery);

        // when
        Boolean result = internalDeliveryService.deliveryStatusCheck(delivery.getId());

        // then
        assertFalse(result);
    }
}
