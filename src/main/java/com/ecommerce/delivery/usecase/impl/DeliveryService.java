package com.ecommerce.delivery.usecase.impl;

import com.ecommerce.common.AesUtil;
import com.ecommerce.common.error.ErrorCode;
import com.ecommerce.common.error.GlobalException;
import com.ecommerce.delivery.adapter.DeliveryAdapter;
import com.ecommerce.delivery.entity.Delivery;
import com.ecommerce.delivery.entity.DeliveryAddress;
import com.ecommerce.delivery.entity.DeliveryStatus;
import com.ecommerce.delivery.repository.DeliveryAddressRepository;
import com.ecommerce.delivery.repository.DeliveryRepository;
import com.ecommerce.delivery.usecase.DeliveryUseCase;
import com.ecommerce.order.entity.OrderLine;
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
    public Delivery processDelivery(OrderLine orderLine, Long deliveryAddressId)
        throws Exception {
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(deliveryAddressId)
            .orElseThrow(
                () -> new GlobalException(ErrorCode.DELIVERY_ADDRESS_NOT_FOUND)
            );

            String street = aesUtil.aesEncode(deliveryAddress.getAddress().getStreet());
            String detailAddress = aesUtil.aesEncode(deliveryAddress.getAddress().getDetailAddress());
            String zipCode = aesUtil.aesEncode(deliveryAddress.getAddress().getZipCode());
            String alias = deliveryAddress.getAddress().getAlias();

            // 배송 요청
            String referenceCode = deliveryAdapter.processDelivery(orderLine.getProductName(),
                orderLine.getQuantity(), street, detailAddress, zipCode, alias);
            if(referenceCode == null) {
                throw new GlobalException(ErrorCode.DELIVERY_FAILED);
            }
            Delivery delivery = Delivery.builder()
                .productId(orderLine.getProductId())
                .productName(orderLine.getProductName())
                .quantity(orderLine.getQuantity())
                .deliveryAddress(deliveryAddress)
                .orderLine(orderLine)
                .status(DeliveryStatus.REQUESTED)
                .referenceCode(referenceCode)
                .build();
            return deliveryRepository.save(delivery);
    }

    @Override
    public void deliveryStatusCheck(Long deliveryId) {
        deliveryRepository.findById(deliveryId)
            .ifPresent(delivery -> {
                if (!delivery.getStatus().name().equals(DeliveryStatus.REQUESTED.name())) {
                    throw new GlobalException(ErrorCode.DELIVERY_STATUS_NOT_REQUESTED);
                }
            });
    }
}
