package com.deliveryservice.usecase.dto;

import lombok.Builder;

@Builder
public record RegisterAddress(
    String street,
    String detailAddress,
    String zipCode,
    String alias,
    String receiver,
    boolean isMainAddress
) {

}
