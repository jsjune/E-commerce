package com.delivery.deliveryapi.usecase.dto;

import lombok.Builder;

@Builder
public record RegisterAddressDto(
    String street,
    String detailAddress,
    String zipCode,
    String alias,
    String receiver,
    boolean mainAddress
) {

}
