package com.payment.paymentcore.application.service.dto;

import java.util.List;

public record PaymentMethodResponseDto(
    List<PaymentMethodListDto> paymentMethods
) {
}
