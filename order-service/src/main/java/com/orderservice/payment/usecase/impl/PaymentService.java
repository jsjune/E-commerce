package com.orderservice.payment.usecase.impl;


import com.orderservice.order.entity.OrderLine;
import com.orderservice.payment.adapter.PaymentAdapter;
import com.orderservice.payment.entity.Payment;
import com.orderservice.payment.entity.PaymentMethod;
import com.orderservice.payment.entity.PaymentStatus;
import com.orderservice.payment.repository.PaymentMethodRepository;
import com.orderservice.payment.repository.PaymentRepository;
import com.orderservice.payment.usecase.PaymentUseCase;
import com.orderservice.utils.AesUtil;
import com.orderservice.utils.error.ErrorCode;
import com.orderservice.utils.error.GlobalException;
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
    public Payment processPayment(Long memberId, OrderLine orderLine,
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
            .memberId(memberId)
            .paymentMethod(paymentMethod)
            .totalPrice(totalPrice)
            .status(PaymentStatus.COMPLETED)
            .referenceCode(referenceCode)
            .build();
        return paymentRepository.save(payment);
    }
}
