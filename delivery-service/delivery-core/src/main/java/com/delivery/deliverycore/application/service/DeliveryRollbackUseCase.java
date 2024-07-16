package com.delivery.deliverycore.application.service;

public interface DeliveryRollbackUseCase {
    void rollbackProcessDelivery(Long deliveryId) throws Exception;
}
