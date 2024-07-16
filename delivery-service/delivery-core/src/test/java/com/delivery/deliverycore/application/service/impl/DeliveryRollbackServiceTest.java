package com.delivery.deliverycore.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.delivery.deliverycore.application.service.DeliveryRollbackUseCase;
import com.delivery.deliverycore.infrastructure.entity.Address;
import com.delivery.deliverycore.infrastructure.entity.Delivery;
import com.delivery.deliverycore.infrastructure.entity.DeliveryAddress;
import com.delivery.deliverycore.infrastructure.entity.DeliveryStatus;
import com.delivery.deliverycore.infrastructure.repository.DeliveryAddressRepository;
import com.delivery.deliverycore.infrastructure.repository.DeliveryRepository;
import com.delivery.deliverycore.testConfig.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeliveryRollbackServiceTest extends IntegrationTestSupport {

    @Autowired
    private DeliveryRollbackUseCase deliveryRollbackUseCase;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @BeforeEach
    void setUp() {
        deliveryAddressRepository.deleteAllInBatch();
        deliveryRepository.deleteAllInBatch();
    }

    @DisplayName("배송 취소 요청")
    @Test
    void rollback_process_delivery() throws Exception {
        // given
        DeliveryAddress deliveryAddress = DeliveryAddress.builder().address(Address.builder().build()).build();
        deliveryAddressRepository.save(deliveryAddress);
        Delivery delivery = Delivery.builder()
            .deliveryAddress(deliveryAddress)
            .build();
        deliveryRepository.save(delivery);

        // when
        deliveryRollbackUseCase.rollbackProcessDelivery(delivery.getId());
        Delivery result = deliveryRepository.findById(delivery.getId()).get();

        // then
        assertEquals(result.getStatus(), DeliveryStatus.CANCELED);
        assertNotNull(result.getReferenceCode());
    }
}
