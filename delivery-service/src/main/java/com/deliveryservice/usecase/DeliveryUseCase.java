package com.deliveryservice.usecase;


import com.deliveryservice.usecase.dto.ProcessDelivery;

public interface DeliveryUseCase {

    Long processDelivery(ProcessDelivery command) throws Exception;

    Boolean deliveryStatusCheck(Long deliveryId);
}
