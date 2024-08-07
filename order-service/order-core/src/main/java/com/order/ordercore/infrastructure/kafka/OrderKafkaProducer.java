package com.order.ordercore.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.ordercore.infrastructure.kafka.event.EventResult;
import com.order.ordercore.infrastructure.kafka.event.ProductOrderEvent;
import com.order.ordercore.infrastructure.kafka.event.SubmitOrderEvent;
import com.order.ordercore.infrastructure.kafka.event.SubmitOrderEvents;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

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
    @Value(value = "${producers.topic6}")
    public String SUBMIT_ORDER_PRODUCT_TOPIC;
    @Value(value = "${producers.topic7}")
    public String SUBMIT_ORDER_CART_TOPIC;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;

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
                throw new RuntimeException("[delivery_request] failed to send", ex);
            }
        });
    }

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

    public void occurRollbackPaymentEvent(EventResult eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(
            ROLLBACK_PAYMENT_TOPIC, json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[rollback_payment_request] sent: {}", eventResult);
            } else {
                log.error("[rollback_payment_request] failed to send: {}", eventResult, ex);
            }
        });
    }

    public void occurRollbackDeliveryEvent(EventResult eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(
            ROLLBACK_DELIVERY_TOPIC, json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[delivery_rollback_request] sent: {}", eventResult);
            } else {
                log.error("[delivery_rollback_request] failed to send: {}", eventResult, ex);
            }
        });
    }

    public void occurSubmitOrderFromProductEvent(SubmitOrderEvent submitEvent) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(submitEvent);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(SUBMIT_ORDER_PRODUCT_TOPIC,
            json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[submit_order_product_request] sent: {}", submitEvent);
            } else {
                log.error("[submit_order_product_request] failed to send: {}", submitEvent, ex);
                throw new RuntimeException("[submit_order_product_request] failed to send",ex);
            }
        });
    }

    public void occurSubmitOrderFromCartEvent(SubmitOrderEvents submitEvents)
        throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(submitEvents);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(SUBMIT_ORDER_CART_TOPIC,
            json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[submit_order_cart_request] sent: {}", submitEvents);
            } else {
                log.error("[submit_order_cart_request] failed to send: {}", submitEvents, ex);
                throw new RuntimeException("[submit_order_cart_request] failed to send",ex);
            }
        });
    }

    public void submitOrderComplete(ProductOrderEvent productOrderEvent)
        throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(productOrderEvent);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(PAYMENT_TOPIC,
            json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[payment_request] sent: {}", productOrderEvent);
            } else {
                log.error("[payment_request] failed to send: {}", productOrderEvent, ex);
                throw new RuntimeException("[payment_request] failed to send", ex);
            }
        });
    }

    public void occurOutBoxEvent(String topic, String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[order_submit_request] sent: {}", message);
            } else {
                log.error("[order_submit_request] failed to send: {}", message, ex);
            }
        });
    }
}
