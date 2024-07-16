package com.delivery.deliverycore.infrastructure.kafka;

import com.delivery.deliverycore.application.service.DeliveryProcessUseCase;
import com.delivery.deliverycore.infrastructure.entity.DeliveryOutBox;
import com.delivery.deliverycore.infrastructure.kafka.event.EventResult;
import com.delivery.deliverycore.infrastructure.repository.DeliveryOutBoxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryKafkaService {
    @Value(value = "${producers.topic1}")
    private String DELIVERY_TOPIC;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DeliveryOutBoxRepository outBoxRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final DeliveryProcessUseCase deliveryProcessUseCase;

    public void handleDelivery(EventResult orderEvent) throws Exception {
        Long deliveryId = deliveryProcessUseCase.processDelivery(orderEvent.mapToCommand());
        int status = deliveryId == -1L ? -1 : 1;
        orderEvent = orderEvent.assignDeliveryIdAndStatus(deliveryId, status);
        eventPublisher.publishEvent(orderEvent);
    }

    public void occurDeliveryFailure(EventResult orderEvent) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(orderEvent);
        DeliveryOutBox outBox = DeliveryOutBox.builder()
            .topic(DELIVERY_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
    }
}
