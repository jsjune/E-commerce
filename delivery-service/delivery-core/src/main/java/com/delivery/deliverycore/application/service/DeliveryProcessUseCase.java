package com.delivery.deliverycore.application.service;

import com.delivery.deliverycore.application.service.dto.ProcessDelivery;

public interface DeliveryProcessUseCase {
    Long processDelivery(ProcessDelivery command) throws Exception;
}
