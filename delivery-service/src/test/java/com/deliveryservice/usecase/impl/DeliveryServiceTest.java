package com.deliveryservice.usecase.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.deliveryservice.IntegrationTestSupport;
import com.deliveryservice.controller.req.AddressRequestDto;
import com.deliveryservice.entity.Delivery;
import com.deliveryservice.entity.DeliveryAddress;
import com.deliveryservice.repository.DeliveryAddressRepository;
import com.deliveryservice.repository.DeliveryRepository;
import com.deliveryservice.usecase.DeliveryAddressUseCase;
import com.deliveryservice.usecase.DeliveryUseCase;
import com.deliveryservice.usecase.dto.ProcessDelivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeliveryServiceTest extends IntegrationTestSupport {
    @Autowired
    private DeliveryUseCase deliveryUseCase;
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
        AddressRequestDto request = getAddressRequest(true);
        deliveryAddressUseCase.registerAddress(memberId, request);
        DeliveryAddress findDeliveryAddress = deliveryAddressRepository.findAll().stream().findFirst()
            .get();

        // when
        ProcessDelivery command = ProcessDelivery.builder()
            .deliveryAddressId(findDeliveryAddress.getId())
            .orderLineId(1L)
            .productId(1L)
            .productName("상품1")
            .quantity(1)
            .build();
        Long deliveryId = deliveryUseCase.processDelivery(command);
        Delivery result = deliveryRepository.findById(deliveryId).orElse(null);

        // then
        assertNotNull(result);
        assertEquals(result.getProductId(), command.productId());
        assertEquals(result.getProductName(), command.productName());
    }

    private static AddressRequestDto getAddressRequest(boolean isMainAddress) {
        return AddressRequestDto.builder()
            .street("서울시 강남구")
            .detailAddress("역삼동")
            .zipCode("12345")
            .alias("집")
            .receiver("홍길동")
            .isMainAddress(isMainAddress)
            .build();
    }
}
