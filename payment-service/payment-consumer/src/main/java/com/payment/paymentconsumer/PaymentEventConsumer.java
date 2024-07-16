package com.payment.paymentconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.paymentcore.application.service.impl.PaymentRollbackService;
import com.payment.paymentcore.infrastructure.kafka.KafkaHealthIndicator;
import com.payment.paymentcore.infrastructure.kafka.PaymentKafkaService;
import com.payment.paymentcore.infrastructure.kafka.event.EventResult;
import com.payment.paymentcore.infrastructure.kafka.event.ProductOrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PaymentRollbackService paymentRollbackService;
    private final PaymentKafkaService paymentKafkaService;
    private final KafkaHealthIndicator kafkaHealthIndicator;

    @KafkaListener(topics = "${consumers.topic1}", groupId = "${consumers.groupId}")
    public void consumePayment(ConsumerRecord<String, String> record) {
        try {
            ProductOrderEvent orderEvent = objectMapper.readValue(record.value(),
                ProductOrderEvent.class);
            if (kafkaHealthIndicator.isKafkaUp()) {
                paymentKafkaService.handlePayment(orderEvent);
            } else {
                log.error("Failed to send payment event");
                paymentKafkaService.occurPaymentFailure(orderEvent);
            }
        } catch (Exception e) {
            log.error("Failed to consume payment event");
            throw new RuntimeException("Failed to consume payment event");
        }
    }

    @KafkaListener(topics = "${consumers.topic2}", groupId = "${consumers.groupId}")
    public void consumeRollbackPayment(ConsumerRecord<String, String> record) {
        try {
            EventResult eventResult = objectMapper.readValue(record.value(), EventResult.class);
            paymentRollbackService.rollbackProcessPayment(eventResult.mapToCommand());
        } catch (Exception e) {
            log.error("Failed to consume rollback payment event");
            throw new RuntimeException("Failed to consume rollback payment event");
        }
    }
}
