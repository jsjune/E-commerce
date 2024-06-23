package com.ecommerce.order.controller.res;

import com.ecommerce.order.entity.OrderLine;
import com.ecommerce.order.entity.ProductOrder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderDetailResponseDto {

    private List<OrderLineDto> orderLines;
    private String orderStatus;
    private int totalPrice;
    private int totalDiscount;

    @Builder
    public OrderDetailResponseDto(List<OrderLine> orderLines, String orderStatus, int totalPrice,
        int totalDiscount) {
        this.orderLines = orderLines.stream().map(OrderLineDto::new).toList();
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.totalDiscount = totalDiscount;
    }

    public OrderDetailResponseDto(ProductOrder productOrders) {
        this.orderLines = productOrders.getOrderLines().stream().map(OrderLineDto::new).toList();
        this.orderStatus = productOrders.getProductOrderStatus().name();
        this.totalPrice = productOrders.getTotalPrice();
        this.totalDiscount = productOrders.getTotalDiscount();
    }
}
