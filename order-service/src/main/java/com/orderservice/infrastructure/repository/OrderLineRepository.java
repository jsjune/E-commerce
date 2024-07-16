package com.orderservice.infrastructure.repository;

import com.orderservice.infrastructure.entity.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {

}
