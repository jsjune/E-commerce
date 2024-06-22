package com.ecommerce.order.controller.res;

import com.ecommerce.order.entity.OrderLine;
import com.ecommerce.product.entity.ProductImage;
import lombok.Getter;

@Getter
public class OrderLineDto {
    private Long orderLineId;
    private Long productId;
    private String productName;
    private int price;
    private int quantity;
    private String thumbnailUrl;
    private String status;
    private Long paymentId;
    private Long deliveryId;

    public OrderLineDto(OrderLine orderLine) {
        this.orderLineId = orderLine.getId();
        this.productId = orderLine.getProduct().getId();
        this.productName = orderLine.getProduct().getName();
        this.price = orderLine.getProduct().getPrice();
        this.quantity = orderLine.getQuantity();
        this.thumbnailUrl = orderLine.getProduct().getProductImages().stream().map(ProductImage::getThumbnailUrl).findFirst().orElse(null);
        this.status = orderLine.getStatus().name();
        this.paymentId = orderLine.getPaymentId();
        this.deliveryId = orderLine.getDeliveryId();
    }
}
