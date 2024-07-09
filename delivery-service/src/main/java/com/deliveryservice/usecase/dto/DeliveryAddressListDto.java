package com.deliveryservice.usecase.dto;

import lombok.Builder;

@Builder
public record DeliveryAddressListDto(
    Long deliveryAddressId,
    String street,
    String detailAddress,
    String zipCode,
    String alias,
    String receiver,
    boolean isMainAddress
) {
}
