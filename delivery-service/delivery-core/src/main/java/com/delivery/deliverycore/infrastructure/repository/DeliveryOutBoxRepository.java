package com.delivery.deliverycore.infrastructure.repository;

import com.delivery.deliverycore.infrastructure.entity.DeliveryOutBox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryOutBoxRepository extends JpaRepository<DeliveryOutBox, Long>,
    CustomDeliveryOutBoxRepository {

}
