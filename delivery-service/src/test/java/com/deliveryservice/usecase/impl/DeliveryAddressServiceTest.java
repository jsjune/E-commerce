package com.deliveryservice.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.deliveryservice.IntegrationTestSupport;
import com.deliveryservice.controller.req.AddressRequestDto;
import com.deliveryservice.controller.res.DeliveryAddressListResponseDto;
import com.deliveryservice.entity.DeliveryAddress;
import com.deliveryservice.repository.DeliveryAddressRepository;
import com.deliveryservice.usecase.DeliveryAddressUseCase;
import com.deliveryservice.usecase.dto.RegisterAddress;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeliveryAddressServiceTest extends IntegrationTestSupport {

    @Autowired
    private DeliveryAddressUseCase deliveryAddressUseCase;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;



    @BeforeEach
    void setUp() {
        deliveryAddressRepository.deleteAllInBatch();
    }

    @DisplayName("등록한 배송지 목록 조회")
    @Test
    void get_member_addresses() throws Exception {
        // given
        long memberId = 1L;
        RegisterAddress command = getAddressRequest(false);
        deliveryAddressUseCase.registerAddress(memberId,command);
        RegisterAddress command2 = getAddressRequest(true);
        deliveryAddressUseCase.registerAddress(memberId,command2);

        // when
        DeliveryAddressListResponseDto result = deliveryAddressUseCase.getAddresses(
            memberId);

        // then
        assertEquals(result.getDeliveryAddresses().size(), 2);
        assertEquals(result.getDeliveryAddresses().get(0).getStreet(), command2.street());
        assertTrue(result.getDeliveryAddresses().get(0).isMainAddress());
    }

    @DisplayName("대표 배송지가 있는데 다시 대표 배송지로 등록할 경우")
    @Test
    void exist_main_address_retry_register_address() throws Exception {
        // given
        long memberId = 1L;
        boolean isMainAddress = true;
        RegisterAddress command = getAddressRequest(isMainAddress);

        // when
        deliveryAddressUseCase.registerAddress(memberId, command);
        deliveryAddressUseCase.registerAddress(memberId, command);

        // then
        List<DeliveryAddress> result = deliveryAddressRepository.findAllByMemberId(
            memberId);
        assertEquals(result.stream().filter(DeliveryAddress::isMainAddress).count(), 1);
        assertFalse(result.get(0).isMainAddress());
        assertTrue(result.get(1).isMainAddress());
    }

    @DisplayName("배송지 등록")
    @Test
    void register_delivery_address() throws Exception {
        // given
        long memberId = 1L;
        boolean isMainAddress = true;
        RegisterAddress command = getAddressRequest(isMainAddress);

        // when
        deliveryAddressUseCase.registerAddress(memberId, command);
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findAllByMemberId(memberId)
            .stream().findFirst().orElse(null);

        // then
        assertNotNull(deliveryAddress);
        assertEquals(deliveryAddress.isMainAddress(), command.isMainAddress());

    }

    private static RegisterAddress getAddressRequest(boolean isMainAddress) {
        return RegisterAddress.builder()
            .street("서울시 강남구")
            .detailAddress("역삼동")
            .zipCode("12345")
            .alias("집")
            .receiver("홍길동")
            .isMainAddress(isMainAddress)
            .build();
    }

}
