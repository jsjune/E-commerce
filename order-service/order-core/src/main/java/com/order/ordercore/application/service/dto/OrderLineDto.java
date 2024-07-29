package com.order.ordercore.application.service.dto;


import lombok.Builder;

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

}
