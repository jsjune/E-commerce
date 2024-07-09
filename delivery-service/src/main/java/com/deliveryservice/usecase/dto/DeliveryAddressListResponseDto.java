package com.deliveryservice.usecase.dto;

import java.util.List;

public record DeliveryAddressListResponseDto(
    List<DeliveryAddressListDto> deliveryAddresses
) {

}
