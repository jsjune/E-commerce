package com.ecommerce.delivery.controller.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberAddressListDto {
    private Long memberAddressId;
    private String street;
    private String detailAddress;
    private String zipCode;
    private String alias;
    private String receiver;
    private boolean isMainAddress;
}
