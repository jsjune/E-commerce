package com.paymentservice.usecase.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

public record PaymentMethodResponseDto(
    List<PaymentMethodListDto> paymentMethods
) {
}
