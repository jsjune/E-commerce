package com.orderservice.usecase.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record OrderDtoFromCart(
    List<Long> cartIds,
    Long paymentMethodId,
    Long deliveryAddressId
) {

}
