package com.orderservice.order.controller.res;


import com.orderservice.order.entity.OrderLine;
import com.orderservice.order.entity.ProductOrder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderDetailResponseDto {
    private Long productOrderId;
    private List<OrderLineDto> orderLines;
    private String orderStatus;
    private int totalPrice;
    private int totalDiscount;

    @Builder
    public OrderDetailResponseDto(Long productOrderId, List<OrderLine> orderLines, String orderStatus, int totalPrice,
        int totalDiscount) {
        this.productOrderId = productOrderId;
        this.orderLines = orderLines.stream().map(OrderLineDto::new).toList();
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.totalDiscount = totalDiscount;
    }

    public OrderDetailResponseDto(ProductOrder productOrders) {
        this.productOrderId = productOrders.getId();
        this.orderLines = productOrders.getOrderLines().stream().map(OrderLineDto::new).toList();
        this.orderStatus = productOrders.getProductOrderStatus().name();
        this.totalPrice = productOrders.getTotalPrice();
        this.totalDiscount = productOrders.getTotalDiscount();
    }
}
