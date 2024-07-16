package com.payment.paymentapi.controller;


import com.payment.paymentapi.common.Response;
import com.payment.paymentapi.controller.req.PaymentMethodRequestDto;
import com.payment.paymentapi.usecase.PaymentMethodUseCase;
import com.payment.paymentapi.usecase.dto.PaymentMethodResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        paymentMethodUseCase.registerPaymentMethod(memberId, request.mapToCommand());
        return Response.success(HttpStatus.OK.value(), null);
    }

    @GetMapping("/methods")
    public Response<PaymentMethodResponseDto> getPaymentMethods(@RequestHeader("Member-Id")Long memberId)
        throws Exception {
        PaymentMethodResponseDto data = paymentMethodUseCase.getPaymentMethods(memberId);
        return Response.success(HttpStatus.OK.value(), data);
    }
}
