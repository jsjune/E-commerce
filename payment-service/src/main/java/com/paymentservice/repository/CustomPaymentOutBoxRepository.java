package com.paymentservice.repository;

import com.paymentservice.entity.PaymentOutBox;
import java.time.LocalDateTime;
import java.util.List;

public interface CustomPaymentOutBoxRepository {
    List<PaymentOutBox> findAllBySuccessFalseNoOffset(LocalDateTime now, int limit);

}
