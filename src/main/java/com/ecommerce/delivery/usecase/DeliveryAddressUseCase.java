package com.ecommerce.delivery.usecase;

import com.ecommerce.delivery.controller.req.AddressRequestDto;
import com.ecommerce.delivery.controller.res.MemberAddressListResponseDto;

public interface DeliveryAddressUseCase {

    void registerAddress(Long memberId, AddressRequestDto request) throws Exception;

    MemberAddressListResponseDto getAddresses(Long memberId) throws Exception;
}
