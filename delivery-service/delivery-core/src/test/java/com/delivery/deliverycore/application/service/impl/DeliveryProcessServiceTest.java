package com.delivery.deliverycore.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.delivery.deliverycore.application.service.DeliveryAddressUseCase;
import com.delivery.deliverycore.application.service.DeliveryProcessUseCase;
import com.delivery.deliverycore.application.service.dto.ProcessDelivery;
import com.delivery.deliverycore.application.service.dto.RegisterAddressDto;
import com.delivery.deliverycore.infrastructure.entity.Delivery;
import com.delivery.deliverycore.infrastructure.entity.DeliveryAddress;
import com.delivery.deliverycore.infrastructure.repository.DeliveryAddressRepository;
import com.delivery.deliverycore.infrastructure.repository.DeliveryRepository;
import com.delivery.deliverycore.testConfig.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeliveryProcessServiceTest extends IntegrationTestSupport {

    @Autowired
    private DeliveryProcessUseCase deliveryProcessUseCase;
    @Autowired
    private DeliveryAddressUseCase deliveryAddressUseCase;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @BeforeEach
    void setUp() {
        deliveryAddressRepository.deleteAllInBatch();
        deliveryRepository.deleteAllInBatch();
    }

    @DisplayName("배송 요청")
    @Test
    void process_delivery() throws Exception {
        // given
        long memberId = 1L;
        RegisterAddressDto request = getAddressRequest(true);
        deliveryAddressUseCase.registerAddress(memberId, request);
        DeliveryAddress findDeliveryAddress = deliveryAddressRepository.findAll().stream().findFirst()
            .get();
        ProcessDelivery command = ProcessDelivery.builder()
            .deliveryAddressId(findDeliveryAddress.getId())
            .orderLineId(1L)
            .productId(1L)
            .productName("상품1")
            .quantity(1L)
            .build();

        // when
        Long deliveryId = deliveryProcessUseCase.processDelivery(command);
        Delivery result = deliveryRepository.findById(deliveryId).orElse(null);

        // then
        assertNotNull(result);
        assertEquals(result.getProductId(), command.productId());
        assertEquals(result.getProductName(), command.productName());
    }

    private static RegisterAddressDto getAddressRequest(boolean mainAddress) {
        return RegisterAddressDto.builder()
            .street("서울시 강남구")
            .detailAddress("역삼동")
            .zipCode("12345")
            .alias("집")
            .receiver("홍길동")
            .mainAddress(mainAddress)
            .build();
    }
}
