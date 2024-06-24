package com.ecommerce.payment.usecase.impl;

import com.ecommerce.common.AesUtil;
import com.ecommerce.common.error.ErrorCode;
import com.ecommerce.common.error.GlobalException;
import com.ecommerce.member.entity.Member;
import com.ecommerce.order.entity.OrderLine;
import com.ecommerce.order.entity.ProductOrder;
import com.ecommerce.payment.adapter.PaymentAdapter;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentMethod;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.PaymentMethodRepository;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.usecase.PaymentUseCase;
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
    public Payment processPayment(Member member, OrderLine orderLine,
        Long paymentMethodId) throws Exception {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
            .orElseThrow(
                () -> new GlobalException(ErrorCode.PAYMENT_METHOD_NOT_FOUND)
            );

        int totalPrice = orderLine.getQuantity() * orderLine.getPrice();
        String bank = aesUtil.aesDecode(paymentMethod.getBank());
        String accountNumber = aesUtil.aesDecode(paymentMethod.getAccountNumber());
        String creditCardNumber = aesUtil.aesDecode(paymentMethod.getCreditCardNumber());
        String referenceCode = paymentAdapter.processPayment(totalPrice, bank, accountNumber,
            creditCardNumber);
        if (referenceCode == null) {
            throw new GlobalException(ErrorCode.PAYMENT_FAILED);
        }

        Payment payment = Payment.builder()
            .orderLine(orderLine)
            .member(member)
            .paymentMethod(paymentMethod)
            .totalPrice(totalPrice)
            .status(PaymentStatus.COMPLETED)
            .referenceCode(referenceCode)
            .build();
        return paymentRepository.save(payment);
    }
}
