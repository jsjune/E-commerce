package com.ecommerce.delivery.usecase;

import com.ecommerce.delivery.entity.Delivery;
import com.ecommerce.order.entity.OrderLine;
import java.util.Optional;

public interface DeliveryUseCase {

    Delivery processDelivery(OrderLine orderLine, Long deliveryAddressId)
        throws Exception;

    void deliveryStatusCheck(Long deliveryId);
}
