package com.deliveryservice.usecase.kafka;

import com.deliveryservice.usecase.DeliveryUseCase;
import com.deliveryservice.usecase.kafka.event.EventResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryKafkaService {

    private final DeliveryUseCase deliveryUseCase;
    private final DeliveryKafkaProducer deliveryKafkaProducer;

    public void handleDelivery(EventResult orderEvent) throws Exception {
        Long deliveryId = deliveryUseCase.processDelivery(orderEvent.mapToCommand());
        int status = deliveryId == -1L ? -1 : 1;
        orderEvent = orderEvent.assignDeliveryIdAndStatus(deliveryId, status);
        deliveryKafkaProducer.occurDeliveryEvent(orderEvent);
    }

    public void occurDeliveryFailure(EventResult orderEvent) throws JsonProcessingException {
        deliveryKafkaProducer.occurDeliveryFailure(orderEvent);
    }
}
