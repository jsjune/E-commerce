package com.delivery.deliveryconsumer;

import com.delivery.deliverycore.application.service.DeliveryRollbackUseCase;
import com.delivery.deliverycore.infrastructure.kafka.DeliveryKafkaService;
import com.delivery.deliverycore.infrastructure.kafka.KafkaHealthIndicator;
import com.delivery.deliverycore.infrastructure.kafka.event.EventResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DeliveryRollbackUseCase deliveryRollbackUseCase;
    private final DeliveryKafkaService deliveryKafkaService;
    private final KafkaHealthIndicator kafkaHealthIndicator;

    @KafkaListener(topics = "${consumers.topic1}", groupId = "${consumers.groupId}")
    public void consumeDelivery(ConsumerRecord<String, String> record) {
        try {
            EventResult orderEvent = objectMapper.readValue(record.value(), EventResult.class);
            if (kafkaHealthIndicator.isKafkaUp()) {
                deliveryKafkaService.handleDelivery(orderEvent);
            } else {
                log.error("Failed to send delivery event");
                deliveryKafkaService.occurDeliveryFailure(orderEvent);
            }
        } catch (Exception e) {
            log.error("Failed to consume delivery event");
            throw new RuntimeException("Failed to consume delivery event");
        }

    }

    @KafkaListener(topics = "${consumers.topic2}", groupId = "${consumers.groupId}")
    public void consumeRollbackDelivery(ConsumerRecord<String, String> record) {
        try {
            EventResult eventResult = objectMapper.readValue(record.value(), EventResult.class);
            deliveryRollbackUseCase.rollbackProcessDelivery(eventResult.deliveryId());
        } catch (Exception e) {
            log.error("Failed to consume rollback delivery event");
            throw new RuntimeException("Failed to consume rollback delivery event");
        }
    }

}
