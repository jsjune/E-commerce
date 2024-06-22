package com.ecommerce.delivery.usecase;

import com.ecommerce.delivery.controller.req.AddressRequestDto;
import com.ecommerce.delivery.controller.res.DeliveryAddressListResponseDto;

public interface DeliveryAddressUseCase {

    void registerAddress(Long memberId, AddressRequestDto request) throws Exception;

    DeliveryAddressListResponseDto getAddresses(Long memberId) throws Exception;
}
