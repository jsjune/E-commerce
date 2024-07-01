package com.payment.paymentconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentservice.usecase.impl.PaymentRollbackService;
import com.paymentservice.usecase.kafka.KafkaHealthIndicator;
import com.paymentservice.usecase.kafka.PaymentKafkaService;
import com.paymentservice.usecase.kafka.event.EventResult;
import com.paymentservice.usecase.kafka.event.ProductOrderEvent;
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
    public void consumePayment(ConsumerRecord<String, String> record)
        throws Exception {
        ProductOrderEvent orderEvent = objectMapper.readValue(record.value(),
            ProductOrderEvent.class);
        if (kafkaHealthIndicator.isKafkaUp()) {
            paymentKafkaService.handlePayment(orderEvent);
        } else {
            log.error("Failed to send payment event");
            paymentKafkaService.occurPaymentFailure(orderEvent);
        }
    }

    @KafkaListener(topics = "${consumers.topic2}", groupId = "${consumers.groupId}")
    public void consumeRollbackPayment(ConsumerRecord<String, String> record)
        throws Exception {
        EventResult eventResult = objectMapper.readValue(record.value(), EventResult.class);
        paymentRollbackService.rollbackProcessPayment(eventResult.mapToCommand());
    }
}
