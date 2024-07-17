package com.order.ordercore.infrastructure.repository;

import com.order.ordercore.infrastructure.entity.OrderOutBox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderOutBoxRepository extends JpaRepository<OrderOutBox, Long>,
    CustomOrderOutBoxRepository {

}
