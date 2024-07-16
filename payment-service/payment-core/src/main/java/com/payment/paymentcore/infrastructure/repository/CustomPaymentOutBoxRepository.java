package com.payment.paymentcore.infrastructure.repository;


import com.payment.paymentcore.infrastructure.entity.PaymentOutBox;
import java.time.LocalDateTime;
import java.util.List;

public interface CustomPaymentOutBoxRepository {
    List<PaymentOutBox> findAllBySuccessFalseNoOffset(LocalDateTime now, int limit);

}
