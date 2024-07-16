package com.payment.paymentcore.infrastructure.repository;


import com.payment.paymentcore.infrastructure.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
