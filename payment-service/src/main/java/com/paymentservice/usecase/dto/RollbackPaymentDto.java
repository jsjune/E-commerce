package com.paymentservice.usecase.dto;

import lombok.Builder;

@Builder
public record RollbackPaymentDto(
    Long paymentId,
    String productName,
    Long quantity,
    Long price,
    Long productId,
    Long memberId
) {

}
