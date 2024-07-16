package com.delivery.deliveryapi.usecase.dto;

import java.util.List;

public record DeliveryAddressListResponseDto(
    List<DeliveryAddressListDto> deliveryAddresses
) {

}
