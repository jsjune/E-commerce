package com.paymentservice.usecase.impl;

import com.paymentservice.adapter.PaymentAdapter;
import com.paymentservice.controller.internal.res.PaymentDto;
import com.paymentservice.entity.Payment;
import com.paymentservice.entity.PaymentMethod;
import com.paymentservice.entity.PaymentStatus;
import com.paymentservice.repository.PaymentMethodRepository;
import com.paymentservice.repository.PaymentRepository;
import com.paymentservice.usecase.PaymentUseCase;
import com.paymentservice.usecase.dto.ProcessPayment;
import com.paymentservice.utils.AesUtil;
import com.paymentservice.utils.error.ErrorCode;
import com.paymentservice.utils.error.GlobalException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService implements PaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentAdapter paymentAdapter;
    private final AesUtil aesUtil;

    @Override
    public PaymentDto processPayment(ProcessPayment command) throws Exception {
        Optional<PaymentMethod> findPaymentMethod = paymentMethodRepository.findById(command.paymentMethodId());
        if (findPaymentMethod.isPresent()) {
            PaymentMethod paymentMethod = findPaymentMethod.get();
            int totalPrice = command.totalPrice();
            String bank = aesUtil.aesDecode(paymentMethod.getBank());
            String accountNumber = aesUtil.aesDecode(paymentMethod.getAccountNumber());
            String creditCardNumber = aesUtil.aesDecode(paymentMethod.getCreditCardNumber());
            String referenceCode = paymentAdapter.processPayment(totalPrice, bank, accountNumber,
                creditCardNumber);
            if (referenceCode == null) {
                return new PaymentDto(null, 0, -1);
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
            Payment savePayment = paymentRepository.save(payment);
            return new PaymentDto(savePayment.getId(), savePayment.getTotalPrice(), 0);
        }
        return null;
    }
}
