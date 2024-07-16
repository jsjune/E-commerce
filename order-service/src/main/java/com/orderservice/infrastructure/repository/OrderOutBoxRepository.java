package com.orderservice.infrastructure.repository;

import com.orderservice.infrastructure.entity.OrderOutBox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderOutBoxRepository extends JpaRepository<OrderOutBox, Long>,CustomOrderOutBoxRepository {

}
