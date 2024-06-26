package com.paymentservice.controller.res;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentMethodResponseDto {
    private List<PaymentMethodListDto> paymentMethods;
}
