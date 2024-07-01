package com.order.orderconsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.usecase.kafka.KafkaHealthIndicator;
import com.orderservice.usecase.kafka.OrderKafkaService;
import com.orderservice.usecase.kafka.event.EventResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderKafkaService orderKafkaService;
    private final KafkaHealthIndicator kafkaHealthIndicator;

    @KafkaListener(topics = "${consumers.topic1}", groupId = "${consumers.groupId}")
    public void consumeOrderFromPayment(ConsumerRecord<String, String> record)
        throws JsonProcessingException {
        EventResult eventResult = objectMapper.readValue(record.value(), EventResult.class);
        if (eventResult.status() == -1) {
            orderKafkaService.handleRollbackOrderFromPayment(eventResult);
        } else {
            if (kafkaHealthIndicator.isKafkaUp()) {
                orderKafkaService.handleOrderFromPayment(eventResult);
            } else {
                log.error("Failed to send payment event");
                orderKafkaService.occurDeliveryFailure(eventResult);
            }
        }
    }

    @KafkaListener(topics = "${consumers.topic2}", groupId = "${consumers.groupId}")
    public void consumeOrderFromDelivery(ConsumerRecord<String, String> record)
        throws JsonProcessingException {
        EventResult eventResult = objectMapper.readValue(record.value(), EventResult.class);
        if (eventResult.status() == -1) {
            if (kafkaHealthIndicator.isKafkaUp()) {
                orderKafkaService.handleRollbackOrderFromDelivery(eventResult);
            } else {
                log.error("Failed to send rollback payment event");
                orderKafkaService.occurRollbackPaymentFailure(eventResult);
            }
        } else {
            if (kafkaHealthIndicator.isKafkaUp()) {
                orderKafkaService.handleOrderFromDelivery(eventResult);
            } else {
                log.error("Failed to send delivery event");
                orderKafkaService.occurProductFailure(eventResult);
            }
        }
    }

    @KafkaListener(topics = "${consumers.topic3}", groupId = "${consumers.groupId}")
    public void consumeOrderFromProduct(ConsumerRecord<String, String> record)
        throws JsonProcessingException {
        EventResult eventResult = objectMapper.readValue(record.value(), EventResult.class);
        if (eventResult.status() == -1) {
            if(kafkaHealthIndicator.isKafkaUp())
                orderKafkaService.handleRollbackOrderFromProduct(eventResult);
            else {
                log.error("Failed to send rollback delivery and payment event");
                orderKafkaService.handleRollbackOrderFailure(eventResult);
            }
        }
    }
}
