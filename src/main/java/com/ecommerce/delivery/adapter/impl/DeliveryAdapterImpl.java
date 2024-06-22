package com.ecommerce.delivery.adapter.impl;

import com.ecommerce.delivery.adapter.DeliveryAdapter;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DeliveryAdapterImpl implements DeliveryAdapter {

    @Override
    public String processDelivery(String productName, int quantity, String street,
        String detailAddress, String zipCode, String alias) {
        // 배송 요청
        return UUID.randomUUID().toString();
    }
}
