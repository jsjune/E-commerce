package com.order.orderapi.usecase.dto;


import java.util.List;
import lombok.Builder;

@Builder
public record OrderDetailResponseDto(
    List<OrderLineDto> orderLines,
    String orderStatus,
    Long totalPrice,
    Long totalDiscount
) {

}
