package com.ecommerce.order.controller.res;

import com.ecommerce.order.entity.ProductOrder;
import java.util.List;
import lombok.Getter;

@Getter
public class OrderListResponseDto {

    private List<OrderDetailResponseDto> orders;

    public OrderListResponseDto(List<ProductOrder> orders) {
        this.orders = orders.stream().map(OrderDetailResponseDto::new).toList();
    }
}
