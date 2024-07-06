package com.orderservice.controller.res;


import com.orderservice.entity.OrderLine;
import com.orderservice.entity.ProductOrder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderDetailResponseDto {
    private List<OrderLineDto> orderLines;
    private String orderStatus;
    private Long totalPrice;
    private Long totalDiscount;

    @Builder
    public OrderDetailResponseDto(Long productOrderId, List<OrderLine> orderLines, String orderStatus, Long totalPrice,
        Long totalDiscount) {
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
