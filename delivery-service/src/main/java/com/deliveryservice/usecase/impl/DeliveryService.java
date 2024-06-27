package com.deliveryservice.usecase.impl;


import com.deliveryservice.adapter.DeliveryAdapter;
import com.deliveryservice.entity.Delivery;
import com.deliveryservice.entity.DeliveryAddress;
import com.deliveryservice.entity.DeliveryStatus;
import com.deliveryservice.repository.DeliveryAddressRepository;
import com.deliveryservice.repository.DeliveryRepository;
import com.deliveryservice.usecase.DeliveryUseCase;
import com.deliveryservice.usecase.dto.ProcessDelivery;
import com.deliveryservice.utils.AesUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryService implements DeliveryUseCase {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final DeliveryAdapter deliveryAdapter;
    private final AesUtil aesUtil;

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

    @Override
    public Boolean deliveryStatusCheck(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
            .map(delivery -> delivery.getStatus().name().equals(DeliveryStatus.REQUESTED.name()))
            .orElse(null);
    }

}
