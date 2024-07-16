package com.payment.paymentapi.usecase.dto;

import java.util.List;

public record PaymentMethodResponseDto(
    List<PaymentMethodListDto> paymentMethods
) {
}
