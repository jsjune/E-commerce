package com.deliveryservice.usecase.kafka;

import com.deliveryservice.entity.DeliveryOutBox;
import com.deliveryservice.repository.DeliveryOutBoxRepository;
import com.deliveryservice.usecase.DeliveryUseCase;
import com.deliveryservice.usecase.kafka.event.EventResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final DeliveryUseCase deliveryUseCase;
    private final DeliveryKafkaProducer deliveryKafkaProducer;

    public void handleDelivery(EventResult orderEvent) throws Exception {
        Long deliveryId = deliveryUseCase.processDelivery(orderEvent.mapToCommand());
        int status = deliveryId == -1L ? -1 : 1;
        orderEvent = orderEvent.assignDeliveryIdAndStatus(deliveryId, status);
        deliveryKafkaProducer.occurDeliveryEvent(orderEvent);
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
