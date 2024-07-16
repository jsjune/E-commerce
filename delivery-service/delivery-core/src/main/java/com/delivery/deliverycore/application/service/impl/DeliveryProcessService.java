package com.delivery.deliverycore.application.service.impl;

import com.delivery.deliverycore.adapter.DeliveryAdapter;
import com.delivery.deliverycore.application.service.DeliveryProcessUseCase;
import com.delivery.deliverycore.application.service.dto.ProcessDelivery;
import com.delivery.deliverycore.application.utils.AesUtil;
import com.delivery.deliverycore.infrastructure.entity.Delivery;
import com.delivery.deliverycore.infrastructure.entity.DeliveryAddress;
import com.delivery.deliverycore.infrastructure.entity.DeliveryStatus;
import com.delivery.deliverycore.infrastructure.repository.DeliveryAddressRepository;
import com.delivery.deliverycore.infrastructure.repository.DeliveryRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryProcessService implements DeliveryProcessUseCase {
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final DeliveryAdapter deliveryAdapter;
    private final AesUtil aesUtil;
    private final DeliveryRepository deliveryRepository;

    @Override
    public Long processDelivery(ProcessDelivery command) throws Exception {
        Optional<DeliveryAddress> findDeliveryAddress = deliveryAddressRepository.findById(
            command.deliveryAddressId());
        if (findDeliveryAddress.isPresent()) {
            DeliveryAddress deliveryAddress = findDeliveryAddress.get();
            String street = aesUtil.aesEncode(deliveryAddress.getAddress().getStreet());
            String detailAddress = aesUtil.aesEncode(
                deliveryAddress.getAddress().getDetailAddress());
            String zipCode = aesUtil.aesEncode(deliveryAddress.getAddress().getZipCode());
            String alias = deliveryAddress.getAddress().getAlias();

            // 배송 요청
            String referenceCode = deliveryAdapter.processDelivery(command.productName(),
                command.quantity(), street, detailAddress, zipCode, alias);
            if (referenceCode == null) {
                return -1L;
            }
            Delivery delivery = Delivery.builder()
                .productId(command.productId())
                .productName(command.productName())
                .quantity(command.quantity())
                .deliveryAddress(deliveryAddress)
                .orderLineId(command.orderLineId())
                .status(DeliveryStatus.REQUESTED)
                .referenceCode(referenceCode)
                .build();
            deliveryRepository.save(delivery);
            return delivery.getId();
        }
        return -1L;
    }
}
