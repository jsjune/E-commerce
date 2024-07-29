package com.delivery.deliverycore.application.service;


import com.delivery.deliverycore.application.service.dto.DeliveryAddressListResponseDto;
import com.delivery.deliverycore.application.service.dto.RegisterAddressDto;

public interface DeliveryAddressUseCase {

    void registerAddress(Long memberId, RegisterAddressDto command) throws Exception;

    DeliveryAddressListResponseDto getAddresses(Long memberId) throws Exception;
}
