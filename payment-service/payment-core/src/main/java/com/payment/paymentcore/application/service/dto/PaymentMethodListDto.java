package com.payment.paymentcore.application.service.dto;

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
