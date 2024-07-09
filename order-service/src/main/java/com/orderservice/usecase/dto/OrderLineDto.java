package com.orderservice.usecase.dto;


import com.orderservice.entity.OrderLine;
import lombok.Builder;
import lombok.Getter;

@Builder
public record OrderLineDto(
    Long productId,
    String productName,
    Long price,
    Long quantity,
    String thumbnailUrl,
    String status,
    Long paymentId,
    Long deliveryId
) {

    public OrderLineDto(OrderLine orderLine) {
        this(
            orderLine.getProductId(),
            orderLine.getProductName(),
            orderLine.getPrice(),
            orderLine.getQuantity(),
            orderLine.getThumbnailUrl(),
            orderLine.getOrderLineStatus().name(),
            orderLine.getPaymentId(),
            orderLine.getDeliveryId()
        );
    }
}
