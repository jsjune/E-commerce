package com.orderservice.delivery.controller.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AddressRequestDto {
    private String street;
    private String detailAddress;
    private String zipCode;
    private String alias;
    private String receiver;
    private boolean isMainAddress;
}
