package com.delivery.deliveryapi.Controller.req;

import com.delivery.deliverycore.application.service.dto.RegisterAddressDto;
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
    public RegisterAddressDto mapToCommand() {
        return RegisterAddressDto.builder()
            .street(street)
            .detailAddress(detailAddress)
            .zipCode(zipCode)
            .alias(alias)
            .receiver(receiver)
            .mainAddress(mainAddress)
            .build();
    }
}
