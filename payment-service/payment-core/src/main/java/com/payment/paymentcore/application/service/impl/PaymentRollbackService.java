package com.payment.paymentcore.application.service.impl;

import com.payment.paymentcore.adapter.PaymentAdapter;
import com.payment.paymentcore.application.service.dto.RollbackPaymentDto;
import com.payment.paymentcore.application.utils.AesUtil;
import com.payment.paymentcore.infrastructure.entity.Payment;
import com.payment.paymentcore.infrastructure.entity.Refund;
import com.payment.paymentcore.infrastructure.entity.RefundStatus;
import com.payment.paymentcore.infrastructure.repository.PaymentRepository;
import com.payment.paymentcore.infrastructure.repository.RefundRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentRollbackService {
    private final AesUtil aesUtil;
    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAdapter paymentAdapter;

    public void rollbackProcessPayment(RollbackPaymentDto command) throws Exception {
        Optional<Payment> findPayment = paymentRepository.findById(command.paymentId());
        if (findPayment.isPresent()) {
            Payment payment = findPayment.get();
            payment.rollbackCancel();

            String bank = aesUtil.aesDecode(payment.getPaymentMethod().getBank());
            String accountNumber = aesUtil.aesDecode(payment.getPaymentMethod().getAccountNumber());
            String creditCardNumber = aesUtil.aesDecode(payment.getPaymentMethod().getCreditCardNumber());
            String referenceCode = paymentAdapter.cancelPayment(
                payment.getTotalPrice(),
                bank,
                accountNumber,
                creditCardNumber);

            Refund refund = Refund.builder()
                .productName(command.productName())
                .quantity(command.quantity())
                .price(command.price())
                .productId(command.productId())
                .memberId(command.memberId())
                .payment(payment)
                .referenceCode(referenceCode)
                .refundStatus(RefundStatus.REFUND)
                .build();
            paymentRepository.save(payment);
            refundRepository.save(refund);
        }
    }
}
