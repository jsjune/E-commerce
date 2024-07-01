package com.orderservice.repository;

import com.orderservice.entity.OrderOutBox;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderOutBoxRepository extends JpaRepository<OrderOutBox, Long> {

    List<OrderOutBox> findAllBySuccessFalse();
}
