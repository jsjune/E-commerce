package com.orderservice.usecase.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.entity.OrderLine;
import com.orderservice.entity.OrderLineStatus;
import com.orderservice.entity.OrderOutBox;
import com.orderservice.entity.ProductOrder;
import com.orderservice.entity.ProductOrderStatus;
import com.orderservice.repository.OrderLineRepository;
import com.orderservice.repository.OrderOutBoxRepository;
import com.orderservice.repository.ProductOrderRepository;
import com.orderservice.usecase.dto.DeliveryEvent;
import com.orderservice.usecase.dto.OrderOutBoxEvent;
import com.orderservice.usecase.dto.PaymentEvent;
import com.orderservice.usecase.dto.RollbackDeliveryEvent;
import com.orderservice.usecase.dto.RollbackPaymentEvent;
import com.orderservice.usecase.impl.OrderRollbackService;
import com.orderservice.usecase.kafka.event.EventResult;
import com.orderservice.usecase.kafka.event.OrderLineEvent;
import com.orderservice.usecase.kafka.event.ProductInfoEvent;
import com.orderservice.usecase.kafka.event.ProductOrderEvent;
import com.orderservice.usecase.kafka.event.SubmitOrderEvent;
import com.orderservice.usecase.kafka.event.SubmitOrderEvents;
import com.orderservice.utils.RedisUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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
    private final OrderRollbackService orderRollbackService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderOutBoxRepository outBoxRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final KafkaHealthIndicator kafkaHealthIndicator;

    public void handleOrderFromPayment(EventResult eventResult) {
        Optional<OrderLine> findOrderLine = orderLineRepository.findById(
            eventResult.orderLine().orderLineId());
        if (findOrderLine.isPresent()) {
            OrderLine orderLine = findOrderLine.get();
            orderLine.assignPayment(eventResult.paymentId());
            orderLineRepository.save(orderLine);
            eventPublisher.publishEvent(new PaymentEvent(eventResult));
        }
    }

    public void handleOrderFromDelivery(EventResult eventResult) {
        Optional<OrderLine> findOrderLine = orderLineRepository.findById(
            eventResult.orderLine().orderLineId());
        if (findOrderLine.isPresent()) {
            OrderLine orderLine = findOrderLine.get();
            orderLine.assignDelivery(eventResult.deliveryId());
            orderLineRepository.save(orderLine);
            eventPublisher.publishEvent(new DeliveryEvent(eventResult));
        }
    }

    public void handleRollbackOrderFromPayment(EventResult eventResult) {
        orderRollbackService.rollbackOrder(eventResult.mapToOrderRollbackDto());
    }

    public void handleRollbackOrderFromDelivery(EventResult eventResult) {
        eventPublisher.publishEvent(new RollbackPaymentEvent(eventResult));
        orderRollbackService.rollbackOrder(eventResult.mapToOrderRollbackDto());
    }

    public void handleRollbackOrderFromProduct(EventResult eventResult)
        throws JsonProcessingException {
        eventPublisher.publishEvent(new RollbackDeliveryEvent(eventResult));
        eventPublisher.publishEvent(new RollbackPaymentEvent(eventResult));
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
        eventPublisher.publishEvent(new OrderOutBoxEvent(outBox.getTopic(), outBox.getMessage()));
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

    public void submitOrderFromProduct(SubmitOrderEvent submitEvent)
        throws JsonProcessingException {
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(submitEvent.memberId())
            .productOrderStatus(ProductOrderStatus.INITIATED)
            .totalPrice(submitEvent.price() * submitEvent.quantity())
            .totalDiscount(0L)
            .build();
        productOrderRepository.save(productOrder);
        OrderLine orderLine = OrderLine.builder()
            .productId(submitEvent.productId())
            .productName(submitEvent.productName())
            .price(submitEvent.price())
            .thumbnailUrl(submitEvent.thumbnailUrl())
            .quantity(submitEvent.quantity())
            .discount(0L)
            .orderLineStatus(OrderLineStatus.INITIATED)
            .build();
        productOrder.addOrderLine(orderLine);
        orderLineRepository.save(orderLine);

        OrderLineEvent orderLineEvent = OrderLineEvent.builder()
            .orderLineId(orderLine.getId())
            .productId(orderLine.getProductId())
            .productName(orderLine.getProductName())
            .price(orderLine.getPrice())
            .discount(orderLine.getDiscount())
            .quantity(orderLine.getQuantity())
            .build();

        ProductOrderEvent productOrderEvent = ProductOrderEvent.builder()
            .productOrderId(productOrder.getId())
            .orderLine(orderLineEvent)
            .memberId(submitEvent.memberId())
            .paymentMethodId(submitEvent.paymentMethodId())
            .deliveryAddressId(submitEvent.deliveryAddressId())
            .build();

        if (kafkaHealthIndicator.isKafkaUp()) {
            eventPublisher.publishEvent(productOrderEvent);
        } else {
            log.error("Failed to send payment event");
            occurPaymentFailure(productOrderEvent);
        }

        productOrder.finalizeOrder(
            ProductOrderStatus.COMPLETED,
            orderLine.getPrice() * orderLine.getQuantity(),
            orderLine.getDiscount()
        );
    }

    public void submitOrderFromCart(SubmitOrderEvents submitEvent) throws JsonProcessingException {
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(submitEvent.memberId())
            .productOrderStatus(ProductOrderStatus.INITIATED)
            .totalDiscount(0L)
            .build();
        productOrderRepository.save(productOrder);
        long totalPrice = 0;
        long totalDiscount = 0;
        for (ProductInfoEvent productInfo : submitEvent.productInfo()) {
            OrderLine orderLine = OrderLine.builder()
                .productOrder(productOrder)
                .productId(productInfo.productId())
                .productName(productInfo.productName())
                .price(productInfo.price())
                .quantity(productInfo.quantity())
                .thumbnailUrl(productInfo.thumbnailUrl())
                .discount(0L)
                .orderLineStatus(OrderLineStatus.INITIATED)
                .build();
            productOrder.addOrderLine(orderLine);
            orderLineRepository.save(orderLine);
            totalPrice += orderLine.getPrice() * orderLine.getQuantity();
            totalDiscount += orderLine.getDiscount();

            OrderLineEvent orderLineEvent = OrderLineEvent.builder()
                .orderLineId(orderLine.getId())
                .productId(orderLine.getProductId())
                .productName(orderLine.getProductName())
                .price(orderLine.getPrice())
                .discount(orderLine.getDiscount())
                .quantity(orderLine.getQuantity())
                .build();
            ProductOrderEvent productOrderEvent = ProductOrderEvent.builder()
                .productOrderId(productOrder.getId())
                .orderLine(orderLineEvent)
                .memberId(submitEvent.memberId())
                .paymentMethodId(submitEvent.paymentMethodId())
                .deliveryAddressId(submitEvent.deliveryAddressId())
                .build();
            if (kafkaHealthIndicator.isKafkaUp()) {
                eventPublisher.publishEvent(productOrderEvent);
            } else {
                log.error("Failed to send payment event");
                occurPaymentFailure(productOrderEvent);
            }
        }
        productOrder.finalizeOrder(
            ProductOrderStatus.COMPLETED,
            totalPrice,
            totalDiscount
        );
    }
}
