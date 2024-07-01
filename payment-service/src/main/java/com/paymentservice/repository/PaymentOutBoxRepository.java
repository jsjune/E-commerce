package com.paymentservice.repository;

import com.paymentservice.entity.PaymentOutBox;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOutBoxRepository extends JpaRepository<PaymentOutBox, Long> {

    List<PaymentOutBox> findAllBySuccessFalse();
}
