package com.delivery.deliverycore.application.service.impl;

import com.delivery.deliverycore.infrastructure.entity.Delivery;
import com.delivery.deliverycore.adapter.DeliveryAdapter;
import com.delivery.deliverycore.infrastructure.repository.DeliveryRepository;
import com.delivery.deliverycore.application.utils.AesUtil;
import com.delivery.deliverycore.application.service.DeliveryRollbackUseCase;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryRollbackService implements DeliveryRollbackUseCase {
    private final DeliveryRepository deliveryRepository;
    private final AesUtil aesUtil;
    private final DeliveryAdapter deliveryAdapter;

    public void rollbackProcessDelivery(Long deliveryId) throws Exception {
        Optional<Delivery> findDelivery = deliveryRepository.findById(deliveryId);
        if (findDelivery.isPresent()) {
            Delivery delivery = findDelivery.get();

            String street = aesUtil.aesEncode(delivery.getDeliveryAddress().getAddress().getStreet());
            String detailAddress = aesUtil.aesEncode(delivery.getDeliveryAddress().getAddress().getDetailAddress());
            String zipCode = aesUtil.aesEncode(delivery.getDeliveryAddress().getAddress().getZipCode());
            String alias = delivery.getDeliveryAddress().getAddress().getAlias();

            String referenceCode = deliveryAdapter.cancelDelivery(delivery.getProductName(),
                delivery.getQuantity(), street, detailAddress, zipCode, alias);
            delivery.rollbackCancel(referenceCode);
            deliveryRepository.save(delivery);
        }
    }
}
