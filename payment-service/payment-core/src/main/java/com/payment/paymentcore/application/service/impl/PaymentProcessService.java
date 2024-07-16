package com.payment.paymentcore.application.service.impl;

import com.payment.paymentcore.adapter.PaymentAdapter;
import com.payment.paymentcore.application.service.PaymentProcessUseCase;
import com.payment.paymentcore.application.service.dto.ProcessPaymentDto;
import com.payment.paymentcore.application.utils.AesUtil;
import com.payment.paymentcore.infrastructure.entity.Payment;
import com.payment.paymentcore.infrastructure.entity.PaymentMethod;
import com.payment.paymentcore.infrastructure.entity.PaymentStatus;
import com.payment.paymentcore.infrastructure.repository.PaymentMethodRepository;
import com.payment.paymentcore.infrastructure.repository.PaymentRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentProcessService implements PaymentProcessUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentAdapter paymentAdapter;
    private final AesUtil aesUtil;

    @Override
    public Long processPayment(ProcessPaymentDto command) throws Exception {
        Optional<PaymentMethod> findPaymentMethod = paymentMethodRepository.findById(
            command.paymentMethodId());
        if (findPaymentMethod.isPresent()) {
            PaymentMethod paymentMethod = findPaymentMethod.get();
            Long totalPrice = command.totalPrice();
            String bank = aesUtil.aesDecode(paymentMethod.getBank());
            String accountNumber = aesUtil.aesDecode(paymentMethod.getAccountNumber());
            String creditCardNumber = aesUtil.aesDecode(paymentMethod.getCreditCardNumber());
            String referenceCode = paymentAdapter.processPayment(totalPrice, bank, accountNumber,
                creditCardNumber);
            if (referenceCode == null) {
                return -1L;
            }

            Payment payment = Payment.builder()
                .orderLineId(command.orderLineId())
                .memberId(command.memberId())
                .paymentMethod(paymentMethod)
                .totalPrice(totalPrice)
                .discountPrice(command.discount())
                .paymentStatus(PaymentStatus.COMPLETED)
                .referenceCode(referenceCode)
                .build();
            Payment savedPayment = paymentRepository.save(payment);
            return savedPayment.getId();
        }
        return -1L;
    }

}
