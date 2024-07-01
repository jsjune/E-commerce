package com.delivery.deliveryconsumer;

import com.deliveryservice.usecase.DeliveryUseCase;
import com.deliveryservice.usecase.impl.DeliveryRollbackService;
import com.deliveryservice.usecase.kafka.DeliveryKafkaProducer;
import com.deliveryservice.usecase.kafka.event.EventResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeliveryEventConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DeliveryUseCase deliveryUseCase;
    private final DeliveryKafkaProducer deliveryKafkaProducer;
    private final DeliveryRollbackService deliveryRollbackService;

    @Transactional
    @KafkaListener(topics = "${consumers.topic1}", groupId = "${consumers.groupId}")
    public void consumeDelivery(ConsumerRecord<String, String> record)
        throws Exception {
        EventResult orderEvent = objectMapper.readValue(record.value(), EventResult.class);
        Long deliveryId = deliveryUseCase.processDelivery(orderEvent.mapToCommand());
        int status = deliveryId == -1L ? -1 : 1;
        orderEvent = orderEvent.assignDeliveryIdAndStatus(deliveryId, status);
        deliveryKafkaProducer.occurDeliveryEvent(orderEvent);
    }

    @Transactional
    @KafkaListener(topics = "${consumers.topic2}", groupId = "${consumers.groupId}")
    public void consumeRollbackDelivery(ConsumerRecord<String, String> record)
        throws Exception {
        EventResult eventResult = objectMapper.readValue(record.value(), EventResult.class);
        deliveryRollbackService.rollbackProcessDelivery(eventResult.deliveryId());
    }

}
