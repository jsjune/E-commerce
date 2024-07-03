package com.orderservice.repository;

import com.orderservice.entity.OrderOutBox;
import java.time.LocalDateTime;
import java.util.List;

public interface CustomOrderOutBoxRepository {

    List<OrderOutBox> findAllBySuccessFalseNoOffset(LocalDateTime now, int limit);
}
