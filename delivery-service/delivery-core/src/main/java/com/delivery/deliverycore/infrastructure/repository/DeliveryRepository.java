package com.delivery.deliverycore.infrastructure.repository;


import com.delivery.deliverycore.infrastructure.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

}
