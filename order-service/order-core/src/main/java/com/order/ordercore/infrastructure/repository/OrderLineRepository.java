package com.order.ordercore.infrastructure.repository;

import com.order.ordercore.infrastructure.entity.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {

}
