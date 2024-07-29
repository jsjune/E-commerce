package com.delivery.deliverycore.application.service.impl;


import com.delivery.deliverycore.application.service.InternalDeliveryUseCase;
import com.delivery.deliverycore.infrastructure.entity.DeliveryStatus;
import com.delivery.deliverycore.infrastructure.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InternalDeliveryService implements InternalDeliveryUseCase {

    private final DeliveryRepository deliveryRepository;

    @Override
    public Boolean deliveryStatusCheck(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
            .map(delivery -> delivery.getStatus().name().equals(DeliveryStatus.REQUESTED.name()))
            .orElse(null);
    }

}
