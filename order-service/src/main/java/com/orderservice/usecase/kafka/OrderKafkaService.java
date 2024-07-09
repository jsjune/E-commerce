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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderKafkaService {
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
    private final ProductOrderRepository productOrderRepository;
    private final OrderLineRepository orderLineRepository;
    private final OrderKafkaProducer orderKafkaProducer;
    private final OrderRollbackService orderRollbackService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderOutBoxRepository outBoxRepository;

    public void handleOrderFromPayment(EventResult eventResult)
        throws JsonProcessingException {
        Optional<OrderLine> findOrderLine = orderLineRepository.findById(
            eventResult.orderLine().orderLineId());
        if (findOrderLine.isPresent()) {
            OrderLine orderLine = findOrderLine.get();
            orderLine.assignPayment(eventResult.paymentId());
            orderLineRepository.save(orderLine);
            orderKafkaProducer.occurDeliveryEvent(eventResult);
        }
    }

    public void handleOrderFromDelivery(EventResult eventResult)
        throws JsonProcessingException {
        Optional<OrderLine> findOrderLine = orderLineRepository.findById(
            eventResult.orderLine().orderLineId());
        if (findOrderLine.isPresent()) {
            OrderLine orderLine = findOrderLine.get();
            orderLine.assignDelivery(eventResult.deliveryId());
            orderLineRepository.save(orderLine);
            orderKafkaProducer.occurProductEvent(eventResult);
        }
    }

    public void handleRollbackOrderFromPayment(EventResult eventResult) {
        orderRollbackService.rollbackOrder(eventResult.mapToOrderRollbackDto());
    }

    public void handleRollbackOrderFromDelivery(EventResult eventResult)
        throws JsonProcessingException {
        orderKafkaProducer.occurRollbackPaymentEvent(eventResult);
        orderRollbackService.rollbackOrder(eventResult.mapToOrderRollbackDto());
    }

    public void handleRollbackOrderFromProduct(EventResult eventResult)
        throws JsonProcessingException {
        orderKafkaProducer.occurRollbackDeliveryEvent(eventResult);
        orderKafkaProducer.occurRollbackPaymentEvent(eventResult);
        orderRollbackService.rollbackOrder(eventResult.mapToOrderRollbackDto());
    }

    public void handleRollbackOrderFailure(EventResult eventResult) throws JsonProcessingException {
        occurRollbackPaymentFailure(eventResult);
        occurRollbackDeliveryFailure(eventResult);
    }

    public void occurDeliveryFailure(EventResult eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        OrderOutBox outBox = OrderOutBox.builder()
            .topic(DELIVERY_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
    }

    public void occurProductFailure(EventResult eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        OrderOutBox outBox = OrderOutBox.builder()
            .topic(PRODUCT_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
    }

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

    public void processOutboxMessage(OrderOutBox outBox) throws JsonProcessingException {
        orderKafkaProducer.occurOutBoxEvent(outBox.getTopic(), outBox.getMessage());
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
}
