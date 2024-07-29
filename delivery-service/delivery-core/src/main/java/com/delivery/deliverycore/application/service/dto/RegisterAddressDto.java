package com.delivery.deliverycore.application.service.dto;

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
