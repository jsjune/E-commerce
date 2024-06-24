package com.orderservice.payment.controller.res;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentMethodResponseDto {
    List<PaymentMethodListDto> paymentMethods;
}
