package com.ecommerce.payment.usecase.impl;

import com.ecommerce.common.AesUtil;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.usecase.AuthUseCase;
import com.ecommerce.payment.controller.req.PaymentMethodRequestDto;
import com.ecommerce.payment.controller.res.PaymentMethodListDto;
import com.ecommerce.payment.controller.res.PaymentMethodResponseDto;
import com.ecommerce.payment.entity.PaymentMethod;
import com.ecommerce.payment.repository.PaymentMethodRepository;
import com.ecommerce.payment.usecase.PaymentMethodUseCase;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentMethodService implements PaymentMethodUseCase {

    private final PaymentMethodRepository paymentMethodRepository;
    private final AuthUseCase authUseCase;
    private final AesUtil aesUtil;

    @Override
    public void registerPaymentMethod(Long memberId, PaymentMethodRequestDto request)
        throws Exception {
        Optional<Member> findMember = authUseCase.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            PaymentMethod paymentMethod = PaymentMethod.builder()
                .member(member)
                .paymentType(request.getPaymentType())
                .bank(aesUtil.aesEncode(request.getBank()))
                .accountNumber(aesUtil.aesEncode(request.getAccountNumber()))
                .creditCardNumber(aesUtil.aesEncode(request.getCreditCardNumber()))
                .build();
            paymentMethodRepository.save(paymentMethod);
        }
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
