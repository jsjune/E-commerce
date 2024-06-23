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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentMethodUseCase paymentMethodUseCase;

    @PostMapping("/methods")
    public Response<Void> registerPaymentMethod(@RequestHeader("Member-Id")Long memberId,
        @RequestBody PaymentMethodRequestDto request) throws Exception {
        paymentMethodUseCase.registerPaymentMethod(memberId, request);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @GetMapping("/methods")
    public Response<PaymentMethodResponseDto> getPaymentMethods(@RequestHeader("Member-Id")Long memberId)
        throws Exception {
        PaymentMethodResponseDto data = paymentMethodUseCase.getPaymentMethods(memberId);
        return Response.success(HttpStatus.OK.value(), data);
    }
}
