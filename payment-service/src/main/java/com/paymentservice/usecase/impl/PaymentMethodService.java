package com.paymentservice.usecase.impl;


import com.paymentservice.usecase.dto.PaymentMethodListDto;
import com.paymentservice.usecase.dto.PaymentMethodResponseDto;
import com.paymentservice.entity.PaymentMethod;
import com.paymentservice.repository.PaymentMethodRepository;
import com.paymentservice.usecase.PaymentMethodUseCase;
import com.paymentservice.usecase.dto.RegisterPaymentMethodDto;
import com.paymentservice.utils.AesUtil;
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
    public void registerPaymentMethod(Long memberId, RegisterPaymentMethodDto command)
        throws Exception {
        PaymentMethod paymentMethod = PaymentMethod.builder()
            .memberId(memberId)
            .paymentType(command.paymentType())
            .bank(aesUtil.aesEncode(command.bank()))
            .accountNumber(aesUtil.aesEncode(command.accountNumber()))
            .creditCardNumber(aesUtil.aesEncode(command.creditCardNumber()))
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
