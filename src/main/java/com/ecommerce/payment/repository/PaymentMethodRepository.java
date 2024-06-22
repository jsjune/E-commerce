package com.ecommerce.payment.repository;

import com.ecommerce.payment.entity.PaymentMethod;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    List<PaymentMethod> findAllByMemberId(Long memberId);
}
