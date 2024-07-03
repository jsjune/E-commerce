package com.deliveryservice.repository;

import com.deliveryservice.entity.DeliveryOutBox;
import java.time.LocalDateTime;
import java.util.List;

public interface CustomDeliveryOutBoxRepository {
    List<DeliveryOutBox> findAllBySuccessFalseNoOffset(LocalDateTime now, int limit);
}
