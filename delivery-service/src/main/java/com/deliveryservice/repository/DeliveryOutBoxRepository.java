package com.deliveryservice.repository;

import com.deliveryservice.entity.DeliveryOutBox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryOutBoxRepository extends JpaRepository<DeliveryOutBox, Long>, CustomDeliveryOutBoxRepository {

}
