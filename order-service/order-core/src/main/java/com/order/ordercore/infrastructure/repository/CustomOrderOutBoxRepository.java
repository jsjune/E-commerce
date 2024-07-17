package com.order.ordercore.infrastructure.repository;

import com.order.ordercore.infrastructure.entity.OrderOutBox;
import java.time.LocalDateTime;
import java.util.List;

public interface CustomOrderOutBoxRepository {

    List<OrderOutBox> findAllBySuccessFalseNoOffset(LocalDateTime now, int limit);
}
