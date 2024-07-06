package com.order.orderconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.entity.OrderLine;
import com.orderservice.entity.OrderLineStatus;
import com.orderservice.entity.ProductOrder;
import com.orderservice.entity.ProductOrderStatus;
import com.orderservice.repository.OrderLineRepository;
import com.orderservice.repository.ProductOrderRepository;
import com.orderservice.usecase.kafka.KafkaHealthIndicator;
import com.orderservice.usecase.kafka.OrderKafkaProducer;
import com.orderservice.usecase.kafka.OrderKafkaService;
import com.orderservice.usecase.kafka.event.EventResult;
import com.orderservice.usecase.kafka.event.OrderLineEvent;
import com.orderservice.usecase.kafka.event.ProductInfoEvent;
import com.orderservice.usecase.kafka.event.ProductOrderEvent;
import com.orderservice.usecase.kafka.event.SubmitOrderEvent;
import com.orderservice.usecase.kafka.event.SubmitOrderEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderKafkaService orderKafkaService;
    private final KafkaHealthIndicator kafkaHealthIndicator;
    private final ProductOrderRepository productOrderRepository;
    private final OrderLineRepository orderLineRepository;
    private final OrderKafkaProducer orderKafkaProducer;
    private final ApplicationEventPublisher applicationEventPublisher;

    @KafkaListener(topics = "submit_order_product_request", groupId = "order")
    public void consumeOrderFromProductSubmit(ConsumerRecord<String, String> record) {
        try {
            SubmitOrderEvent submitEvent = objectMapper.readValue(record.value(),
                SubmitOrderEvent.class);
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
                applicationEventPublisher.publishEvent(productOrderEvent);
            } else {
                log.error("Failed to send payment event");
                orderKafkaProducer.occurPaymentFailure(productOrderEvent);
            }
            productOrder.finalizeOrder(
                ProductOrderStatus.COMPLETED,
                orderLine.getPrice() * orderLine.getQuantity(),
                orderLine.getDiscount()
            );
        } catch (Exception e) {
            log.error("Failed to consume submit", e);
        }
    }

    @KafkaListener(topics = "submit_order_cart_request", groupId = "order")
    public void consumeOrderFromCartSubmit(ConsumerRecord<String, String> record) {
        try {
            SubmitOrderEvents submitEvent = objectMapper.readValue(record.value(),
                SubmitOrderEvents.class);
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
                    applicationEventPublisher.publishEvent(productOrderEvent);
                } else {
                    log.error("Failed to send payment event");
                    orderKafkaProducer.occurPaymentFailure(productOrderEvent);
                }
            }
            productOrder.finalizeOrder(
                ProductOrderStatus.COMPLETED,
                totalPrice,
                totalDiscount
            );

        } catch (Exception e) {
            log.error("Failed to consume submit", e);
        }
    }

    @KafkaListener(topics = "${consumers.topic1}", groupId = "${consumers.groupId}")
    public void consumeOrderFromPayment(ConsumerRecord<String, String> record) {
        try {
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
        } catch (Exception e) {
            log.error("Failed to consume order from payment", e);
        }
    }

    @KafkaListener(topics = "${consumers.topic2}", groupId = "${consumers.groupId}")
    public void consumeOrderFromDelivery(ConsumerRecord<String, String> record) {
        try {
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
        } catch (Exception e) {
            log.error("Failed to consume order from delivery", e);
        }
    }

    @KafkaListener(topics = "${consumers.topic3}", groupId = "${consumers.groupId}")
    public void consumeOrderFromProduct(ConsumerRecord<String, String> record) {
        try {
            EventResult eventResult = objectMapper.readValue(record.value(), EventResult.class);
            if (eventResult.status() == -1) {
                if (kafkaHealthIndicator.isKafkaUp()) {
                    orderKafkaService.handleRollbackOrderFromProduct(eventResult);
                } else {
                    log.error("Failed to send rollback delivery and payment event");
                    orderKafkaService.handleRollbackOrderFailure(eventResult);
                }
            }
        } catch (Exception e) {
            log.error("Failed to consume order from product", e);
        }
    }

}
