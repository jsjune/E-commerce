package com.deliveryservice.usecase;


import com.deliveryservice.controller.req.AddressRequestDto;
import com.deliveryservice.controller.res.DeliveryAddressListResponseDto;

public interface DeliveryAddressUseCase {

    void registerAddress(Long memberId, AddressRequestDto request) throws Exception;

    DeliveryAddressListResponseDto getAddresses(Long memberId) throws Exception;
}
