package com.orderservice.usecase.dto;


import com.orderservice.infrastructure.entity.ProductOrder;
import java.util.List;
import lombok.Builder;

@Builder
public record OrderDetailResponseDto(
    List<OrderLineDto> orderLines,
    String orderStatus,
    Long totalPrice,
    Long totalDiscount
) {

    public OrderDetailResponseDto(ProductOrder productOrders) {
        this(
            productOrders.getOrderLines().stream().map(OrderLineDto::new).toList(),
            productOrders.getProductOrderStatus().name(),
            productOrders.getTotalPrice(),
            productOrders.getTotalDiscount()
        );
    }
}
