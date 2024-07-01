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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
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
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OrderOutBoxRepository outBoxRepository;
    private final ProductOrderRepository productOrderRepository;
    private final OrderLineRepository orderLineRepository;
    private final KafkaHealthIndicator kafkaHealthIndicator;
    private final OrderRollbackService orderRollbackService;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void occurDeliveryFailure(EventResult eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        OrderOutBox outBox = OrderOutBox.builder()
            .topic(DELIVERY_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void occurProductFailure(EventResult eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        OrderOutBox outBox = OrderOutBox.builder()
            .topic(PRODUCT_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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

    @TransactionalEventListener
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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

    @Transactional
    @Scheduled(fixedRate = 10000)
    public void retry() throws JsonProcessingException {
        log.info("kafka health check and retrying...");
        List<OrderOutBox> findOrderOutBoxes = outBoxRepository.findAllBySuccessFalse();
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
        }else if (outBox.getTopic().equals(ROLLBACK_PAYMENT_TOPIC) || outBox.getTopic().equals(ROLLBACK_DELIVERY_TOPIC)) {
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
}



