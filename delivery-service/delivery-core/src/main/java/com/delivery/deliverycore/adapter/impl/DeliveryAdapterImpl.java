package com.delivery.deliverycore.adapter.impl;

import com.delivery.deliverycore.adapter.DeliveryAdapter;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DeliveryAdapterImpl implements DeliveryAdapter {

    @Override
    public String processDelivery(String productName, Long quantity, String street,
        String detailAddress, String zipCode, String alias) {
        // actual delivery with external system
        return UUID.randomUUID().toString();
    }

    @Override
    public String cancelDelivery(String productName, Long quantity, String street,
        String detailAddress, String zipCode, String alias) {
        // actual delivery cancel with external system
        return UUID.randomUUID().toString();
    }
}
