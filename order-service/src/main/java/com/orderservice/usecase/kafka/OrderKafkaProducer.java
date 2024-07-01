package com.orderservice.usecase.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.usecase.kafka.event.EventResult;
import com.orderservice.usecase.kafka.event.ProductOrderEvent;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderKafkaProducer {

    @Value(value = "${producers.topic1}")
    public String PAYMENT_TOPIC;
    @Value(value = "${producers.topic2}")
    public String DELIVERY_TOPIC;
    @Value(value = "${producers.topic3}")
    public String PRODUCT_TOPIC;
    @Value(value = "${producers.topic4}")
    public String ROLLBACK_PAYMENT_TOPIC;
    @Value(value = "${producers.topic5}")
    public String ROLLBACK_DELIVERY_TOPIC;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void occurPaymentEvent(ProductOrderEvent productOrderEvent)
        throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(productOrderEvent);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(PAYMENT_TOPIC,
            json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[payment_request] sent: {}", productOrderEvent);
            } else {
                log.error("[payment_request] failed to send: {}", productOrderEvent, ex);
            }
        });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void occurDeliveryEvent(EventResult productOrderEvent)
        throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(productOrderEvent);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(DELIVERY_TOPIC,
            json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[delivery_request] sent: {}", productOrderEvent);
            } else {
                log.error("[delivery_request] failed to send: {}", productOrderEvent, ex);
            }
        });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void occurProductEvent(EventResult eventResult)
        throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(PRODUCT_TOPIC,
            json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[product_request] sent: {}", eventResult);
            } else {
                log.error("[product_request] failed to send: {}", eventResult, ex);
            }
        });

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void occurRollbackPaymentEvent(EventResult eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(ROLLBACK_PAYMENT_TOPIC, json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[rollback_payment_request] sent: {}", eventResult);
            } else {
                log.error("[rollback_payment_request] failed to send: {}", eventResult, ex);
            }
        });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void occurRollbackDeliveryEvent(EventResult eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(ROLLBACK_DELIVERY_TOPIC, json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[rollback_delivery_request] sent: {}", eventResult);
            } else {
                log.error("[rollback_delivery_request] failed to send: {}", eventResult, ex);
            }
        });
    }
}

