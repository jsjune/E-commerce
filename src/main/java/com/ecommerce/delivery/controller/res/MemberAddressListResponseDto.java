package com.ecommerce.delivery.controller.res;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberAddressListResponseDto {
    private List<MemberAddressListDto> memberAddresses;

}
