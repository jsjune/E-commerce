package com.payment.paymentcore.infrastructure.repository;


import com.payment.paymentcore.infrastructure.entity.PaymentMethod;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    List<PaymentMethod> findAllByMemberId(Long memberId);
}
