package com.delivery.deliverycore.infrastructure.repository;

import com.delivery.deliverycore.infrastructure.entity.DeliveryOutBox;
import java.time.LocalDateTime;
import java.util.List;

public interface CustomDeliveryOutBoxRepository {
    List<DeliveryOutBox> findAllBySuccessFalseNoOffset(LocalDateTime now, int limit);
}
