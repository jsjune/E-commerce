package com.orderservice.payment.usecase.impl;


import com.orderservice.payment.controller.req.PaymentMethodRequestDto;
import com.orderservice.payment.controller.res.PaymentMethodListDto;
import com.orderservice.payment.controller.res.PaymentMethodResponseDto;
import com.orderservice.payment.entity.PaymentMethod;
import com.orderservice.payment.repository.PaymentMethodRepository;
import com.orderservice.payment.usecase.PaymentMethodUseCase;
import com.orderservice.utils.AesUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentMethodService implements PaymentMethodUseCase {

    private final PaymentMethodRepository paymentMethodRepository;
    private final AesUtil aesUtil;

    @Override
    public void registerPaymentMethod(Long memberId, PaymentMethodRequestDto request)
        throws Exception {
        PaymentMethod paymentMethod = PaymentMethod.builder()
            .memberId(memberId)
            .paymentType(request.getPaymentType())
            .bank(aesUtil.aesEncode(request.getBank()))
            .accountNumber(aesUtil.aesEncode(request.getAccountNumber()))
            .creditCardNumber(aesUtil.aesEncode(request.getCreditCardNumber()))
            .build();
        paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public PaymentMethodResponseDto getPaymentMethods(Long memberId) throws Exception {
        List<PaymentMethod> findPaymentMethod = paymentMethodRepository.findAllByMemberId(memberId);
        List<PaymentMethodListDto> paymentMethods = new ArrayList<>();
        for (PaymentMethod paymentMethod : findPaymentMethod) {
            PaymentMethodListDto paymentMethodList = PaymentMethodListDto.builder()
                .paymentId(paymentMethod.getId())
                .paymentType(paymentMethod.getPaymentType().name())
                .bank(aesUtil.aesDecode(paymentMethod.getBank()))
                .accountNumber(aesUtil.aesDecode(paymentMethod.getAccountNumber()))
                .creditCardNumber(aesUtil.aesDecode(paymentMethod.getCreditCardNumber()))
                .build();
            paymentMethods.add(paymentMethodList);
        }
        return new PaymentMethodResponseDto(paymentMethods);
    }
}
