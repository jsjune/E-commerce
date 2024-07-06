package com.orderservice.controller.res;


import com.orderservice.entity.OrderLine;
import lombok.Getter;

@Getter
public class OrderLineDto {
    private Long productId;
    private String productName;
    private Long price;
    private Long quantity;
    private String thumbnailUrl;
    private String status;
    private Long paymentId;
    private Long deliveryId;

    public OrderLineDto(OrderLine orderLine) {
        this.productId = orderLine.getProductId();
        this.productName = orderLine.getProductName();
        this.price = orderLine.getPrice();
        this.quantity = orderLine.getQuantity();
        this.thumbnailUrl = orderLine.getThumbnailUrl();
        this.status = orderLine.getOrderLineStatus().name();
        this.paymentId = orderLine.getPaymentId();
        this.deliveryId = orderLine.getDeliveryId();
    }
}
