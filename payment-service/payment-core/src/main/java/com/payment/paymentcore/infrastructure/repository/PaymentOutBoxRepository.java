package com.payment.paymentcore.infrastructure.repository;


import com.payment.paymentcore.infrastructure.entity.PaymentOutBox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOutBoxRepository extends JpaRepository<PaymentOutBox, Long>,CustomPaymentOutBoxRepository {

}
