package com.paymentservice.usecase.impl;



import com.paymentservice.controller.req.PaymentMethodRequestDto;
import com.paymentservice.controller.res.PaymentMethodResponseDto;
import com.paymentservice.controller.res.PaymentMethodListDto;
import com.paymentservice.entity.PaymentMethod;
import com.paymentservice.repository.PaymentMethodRepository;
import com.paymentservice.usecase.PaymentMethodUseCase;
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
