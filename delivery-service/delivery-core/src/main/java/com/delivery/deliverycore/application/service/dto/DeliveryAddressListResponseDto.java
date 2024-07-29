package com.delivery.deliverycore.application.service.dto;

import java.util.List;

public record DeliveryAddressListResponseDto(
    List<DeliveryAddressListDto> deliveryAddresses
) {

}
