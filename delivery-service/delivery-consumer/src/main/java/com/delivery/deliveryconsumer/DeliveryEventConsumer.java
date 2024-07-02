package com.delivery.deliveryconsumer;

import com.deliveryservice.usecase.kafka.DeliveryKafkaService;
import com.deliveryservice.usecase.kafka.KafkaHealthIndicator;
import com.deliveryservice.usecase.impl.DeliveryRollbackService;
import com.deliveryservice.usecase.kafka.event.EventResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DeliveryRollbackService deliveryRollbackService;
    private final DeliveryKafkaService deliveryKafkaService;
    private final KafkaHealthIndicator kafkaHealthIndicator;

    @KafkaListener(topics = "${consumers.topic1}", groupId = "${consumers.groupId}")
    public void consumeDelivery(ConsumerRecord<String, String> record)
        throws Exception {
        EventResult orderEvent = objectMapper.readValue(record.value(), EventResult.class);
        if (kafkaHealthIndicator.isKafkaUp()) {
            deliveryKafkaService.handleDelivery(orderEvent);
        } else {
            log.error("Failed to send delivery event");
            deliveryKafkaService.occurDeliveryFailure(orderEvent);
        }
    }

    @KafkaListener(topics = "${consumers.topic2}", groupId = "${consumers.groupId}")
    public void consumeRollbackDelivery(ConsumerRecord<String, String> record)
        throws Exception {
        EventResult eventResult = objectMapper.readValue(record.value(), EventResult.class);
        deliveryRollbackService.rollbackProcessDelivery(eventResult.deliveryId());
    }

}
