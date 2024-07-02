package com.deliveryservice.usecase.impl;

import com.deliveryservice.adapter.DeliveryAdapter;
import com.deliveryservice.entity.Delivery;
import com.deliveryservice.repository.DeliveryRepository;
import com.deliveryservice.utils.AesUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryRollbackService {
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
