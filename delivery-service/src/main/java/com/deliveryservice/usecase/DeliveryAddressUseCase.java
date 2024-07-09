package com.deliveryservice.usecase;


import com.deliveryservice.usecase.dto.DeliveryAddressListResponseDto;
import com.deliveryservice.usecase.dto.RegisterAddress;

public interface DeliveryAddressUseCase {

    void registerAddress(Long memberId, RegisterAddress command) throws Exception;

    DeliveryAddressListResponseDto getAddresses(Long memberId) throws Exception;
}
