package com.orderservice.delivery.controller.res;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeliveryAddressListResponseDto {
    private List<DeliveryAddressListDto> deliveryAddresses;

}
