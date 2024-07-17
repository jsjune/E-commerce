package com.order.orderapi.usecase.dto;


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
