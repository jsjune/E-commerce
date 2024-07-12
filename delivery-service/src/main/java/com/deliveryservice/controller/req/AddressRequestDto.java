package com.deliveryservice.controller.req;

import com.deliveryservice.usecase.dto.RegisterAddress;
import lombok.Builder;

@Builder
public record AddressRequestDto(
    String street,
    String detailAddress,
    String zipCode,
    String alias,
    String receiver,
    boolean mainAddress
) {
    public RegisterAddress mapToCommand() {
        return RegisterAddress.builder()
            .street(street)
            .detailAddress(detailAddress)
            .zipCode(zipCode)
            .alias(alias)
            .receiver(receiver)
            .mainAddress(mainAddress)
            .build();
    }
}
