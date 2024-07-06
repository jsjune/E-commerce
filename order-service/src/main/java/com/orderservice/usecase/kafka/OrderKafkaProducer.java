package com.orderservice.usecase.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.entity.OrderLine;
import com.orderservice.entity.OrderOutBox;
import com.orderservice.entity.ProductOrder;
import com.orderservice.entity.ProductOrderStatus;
import com.orderservice.repository.OrderLineRepository;
import com.orderservice.repository.OrderOutBoxRepository;
import com.orderservice.repository.ProductOrderRepository;
import com.orderservice.usecase.impl.OrderRollbackService;
import com.orderservice.usecase.kafka.event.EventResult;
import com.orderservice.usecase.kafka.event.ProductOrderEvent;
import com.orderservice.usecase.kafka.event.SubmitOrderEvent;
import com.orderservice.usecase.kafka.event.SubmitOrderEvents;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
    @Value(value = "${producers.topic6}")
    public String SUBMIT_ORDER_PRODUCT_TOPIC;
    @Value(value = "${producers.topic7}")
    public String SUBMIT_ORDER_CART_TOPIC;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OrderOutBoxRepository outBoxRepository;
    private final ProductOrderRepository productOrderRepository;
    private final OrderLineRepository orderLineRepository;
    private final KafkaHealthIndicator kafkaHealthIndicator;
    private final OrderRollbackService orderRollbackService;

    @Transactional
    public void occurPaymentFailure(ProductOrderEvent productOrderEvent)
        throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(productOrderEvent);
        OrderOutBox outBox = OrderOutBox.builder()
            .topic(PAYMENT_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
    }

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

    @Transactional
    public void occurDeliveryFailure(EventResult eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        OrderOutBox outBox = OrderOutBox.builder()
            .topic(DELIVERY_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
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

    @Transactional
    public void occurProductFailure(EventResult eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        OrderOutBox outBox = OrderOutBox.builder()
            .topic(PRODUCT_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
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

    @Transactional
    public void occurRollbackPaymentFailure(EventResult eventResult)
        throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        OrderOutBox outBox = OrderOutBox.builder()
            .topic(ROLLBACK_PAYMENT_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
    }

    public void occurRollbackDeliveryEvent(EventResult eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(
            ROLLBACK_DELIVERY_TOPIC, json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[rollback_delivery_request] sent: {}", eventResult);
            } else {
                log.error("[rollback_delivery_request] failed to send: {}", eventResult, ex);
            }
        });
    }

    @Transactional
    public void occurRollbackDeliveryFailure(EventResult eventResult)
        throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        OrderOutBox outBox = OrderOutBox.builder()
            .topic(ROLLBACK_DELIVERY_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void retry() throws JsonProcessingException {
        log.info("kafka health check and retrying...");
        List<OrderOutBox> findOrderOutBoxes = outBoxRepository.findAllBySuccessFalseNoOffset(
            LocalDateTime.now(), 5);
        for (OrderOutBox outBox : findOrderOutBoxes) {
            if (kafkaHealthIndicator.isKafkaUp()) {
                processOutboxMessage(outBox);
                outBoxRepository.delete(outBox);
            }
        }
    }

    private void processOutboxMessage(OrderOutBox outBox) throws JsonProcessingException {
        kafkaTemplate.send(outBox.getTopic(), outBox.getMessage());
        if (outBox.getTopic().equals(PAYMENT_TOPIC)) {
            processPaymentEvent(outBox);
        } else if (outBox.getTopic().equals(DELIVERY_TOPIC)) {
            processDeliveryEvent(outBox);
        } else if (outBox.getTopic().equals(PRODUCT_TOPIC)) {
            processProductEvent(outBox);
        } else if (outBox.getTopic().equals(ROLLBACK_PAYMENT_TOPIC) || outBox.getTopic()
            .equals(ROLLBACK_DELIVERY_TOPIC)) {
            processRollbackEvent(outBox);
        }
    }

    private void processPaymentEvent(OrderOutBox outBox) throws JsonProcessingException {
        ProductOrderEvent result = objectMapper.readValue(outBox.getMessage(),
            ProductOrderEvent.class);
        Optional<ProductOrder> findProductOrder = productOrderRepository.findById(
            result.productOrderId());
        if (findProductOrder.isPresent()) {
            ProductOrder productOrder = findProductOrder.get();
            long finalTotalPrice = 0L;
            long totalDiscount = 0L;
            for (OrderLine orderLine : productOrder.getOrderLines()) {
                totalDiscount += orderLine.getDiscount();
                finalTotalPrice += orderLine.getPrice() * orderLine.getQuantity();
            }
            productOrder.finalizeOrder(ProductOrderStatus.COMPLETED, finalTotalPrice,
                totalDiscount);
            productOrderRepository.save(productOrder);
        }
    }

    private void processDeliveryEvent(OrderOutBox outBox) throws JsonProcessingException {
        EventResult result = objectMapper.readValue(outBox.getMessage(), EventResult.class);
        Optional<OrderLine> findOrderLine = orderLineRepository.findById(
            result.orderLine().orderLineId());
        if (findOrderLine.isPresent()) {
            OrderLine orderLine = findOrderLine.get();
            orderLine.assignPayment(result.paymentId());
            orderLineRepository.save(orderLine);
        }
    }

    private void processProductEvent(OrderOutBox outBox) throws JsonProcessingException {
        EventResult result = objectMapper.readValue(outBox.getMessage(), EventResult.class);
        Optional<OrderLine> findOrderLine = orderLineRepository.findById(
            result.orderLine().orderLineId());
        if (findOrderLine.isPresent()) {
            OrderLine orderLine = findOrderLine.get();
            orderLine.assignDelivery(result.deliveryId());
            orderLineRepository.save(orderLine);
        }
    }

    private void processRollbackEvent(OrderOutBox outBox) throws JsonProcessingException {
        EventResult result = objectMapper.readValue(outBox.getMessage(), EventResult.class);
        orderRollbackService.rollbackOrder(result.mapToOrderRollbackDto());
    }

    public void occurSubmitOrderFromProductEvent(SubmitOrderEvent submitEvent) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(submitEvent);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(SUBMIT_ORDER_PRODUCT_TOPIC,
            json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[order_submit_request] sent: {}", submitEvent);
            } else {
                log.error("[order_submit_request] failed to send: {}", submitEvent, ex);
            }
        });
    }

    public void occurSubmitOrderFromProductEventFailure(SubmitOrderEvent submitEvent)
        throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(submitEvent);
        OrderOutBox outBox = OrderOutBox.builder()
            .topic(SUBMIT_ORDER_PRODUCT_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
    }

    public void occurSubmitOrderFromCartEvent(SubmitOrderEvents submitEvents)
        throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(submitEvents);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(SUBMIT_ORDER_CART_TOPIC,
            json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[order_submit_request] sent: {}", submitEvents);
            } else {
                log.error("[order_submit_request] failed to send: {}", submitEvents, ex);
            }
        });
    }

    public void occurSubmitOrderFromCartEventFailure(SubmitOrderEvents submitEvents)
        throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(submitEvents);
        OrderOutBox outBox = OrderOutBox.builder()
            .topic(SUBMIT_ORDER_CART_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void submitOrderFromCartComplete(ProductOrderEvent productOrderEvent)
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
}
