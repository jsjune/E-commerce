package com.paymentservice.usecase.dto;

import lombok.Builder;

@Builder
public record PaymentMethodListDto(
    Long paymentId,
    String paymentType,
    String bank,
    String accountNumber,
    String creditCardNumber
) {
}
