package com.payment.paymentcore.infrastructure.repository;


import com.payment.paymentcore.infrastructure.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {

}
