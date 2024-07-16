package com.orderservice.infrastructure.repository;

import com.orderservice.infrastructure.entity.OrderOutBox;
import java.time.LocalDateTime;
import java.util.List;

public interface CustomOrderOutBoxRepository {

    List<OrderOutBox> findAllBySuccessFalseNoOffset(LocalDateTime now, int limit);
}
