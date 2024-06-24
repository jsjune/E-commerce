package com.orderservice.delivery.usecase;

import com.orderservice.delivery.controller.req.AddressRequestDto;
import com.orderservice.delivery.controller.res.DeliveryAddressListResponseDto;

public interface DeliveryAddressUseCase {

    void registerAddress(Long memberId, AddressRequestDto request) throws Exception;

    DeliveryAddressListResponseDto getAddresses(Long memberId) throws Exception;
}
