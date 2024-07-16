package com.delivery.deliveryapi.usecase;


import com.delivery.deliveryapi.usecase.dto.DeliveryAddressListResponseDto;
import com.delivery.deliveryapi.usecase.dto.RegisterAddressDto;

public interface DeliveryAddressUseCase {

    void registerAddress(Long memberId, RegisterAddressDto command) throws Exception;

    DeliveryAddressListResponseDto getAddresses(Long memberId) throws Exception;
}
