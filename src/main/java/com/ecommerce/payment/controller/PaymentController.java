package com.ecommerce.payment.controller;

import com.ecommerce.common.Response;
import com.ecommerce.member.auth.LoginUser;
import com.ecommerce.payment.controller.req.PaymentMethodRequestDto;
import com.ecommerce.payment.controller.res.PaymentMethodResponseDto;
import com.ecommerce.payment.usecase.PaymentMethodUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentMethodUseCase paymentMethodUseCase;

    @PostMapping("/payment/methods")
    public Response<Void> registerPaymentMethod(@AuthenticationPrincipal LoginUser loginUser,
        @RequestBody PaymentMethodRequestDto request) throws Exception {
        paymentMethodUseCase.registerPaymentMethod(loginUser.getMember().getId(), request);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @GetMapping("/payment/methods")
    public Response<PaymentMethodResponseDto> getPaymentMethods(@AuthenticationPrincipal LoginUser loginUser)
        throws Exception {
        PaymentMethodResponseDto data = paymentMethodUseCase.getPaymentMethods(loginUser.getMember().getId());
        return Response.success(HttpStatus.OK.value(), data);
    }
}
