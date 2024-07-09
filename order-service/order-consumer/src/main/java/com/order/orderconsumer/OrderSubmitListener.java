//package com.order.orderconsumer;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.orderservice.adapter.MemberClient;
//import com.orderservice.entity.OrderLine;
//import com.orderservice.entity.OrderLineStatus;
//import com.orderservice.entity.ProductOrder;
//import com.orderservice.entity.ProductOrderStatus;
//import com.orderservice.repository.OrderLineRepository;
//import com.orderservice.repository.ProductOrderRepository;
//import com.orderservice.usecase.kafka.KafkaHealthIndicator;
//import com.orderservice.usecase.kafka.OrderKafkaProducer;
//import com.orderservice.usecase.kafka.event.OrderLineEvent;
//import com.orderservice.usecase.kafka.event.ProductOrderEvent;
//import com.orderservice.usecase.kafka.event.SubmitOrderEvent;
//import java.time.Duration;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@RequiredArgsConstructor
//@Component
//@EnableScheduling
//@Slf4j
//public class OrderSubmitListener {
//    @Value("${producers.topic6}")
//    private String topic;
//    private final ObjectMapper objectMapper;
//    private final KafkaConsumer<String, String> kafkaConsumer;
//    private final ProductOrderRepository productOrderRepository;
//    private final OrderLineRepository orderLineRepository;
//    private final KafkaHealthIndicator kafkaHealthIndicator;
//    private final OrderKafkaProducer orderKafkaProducer;
//    private final MemberClient memberClient;
//
//    @Scheduled(fixedDelay = 1000)
//    public void listen() throws JsonProcessingException {
//        log.info("listen...");
//        kafkaConsumer.subscribe(List.of(topic));
//        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(1));
//        for (ConsumerRecord<String, String> record : records) {
//            SubmitOrderEvent submitEvent = objectMapper.readValue(record.value(),
//                SubmitOrderEvent.class);
//            ProductOrder productOrder = ProductOrder.builder()
//                .memberId(submitEvent.memberId())
//                .productOrderStatus(ProductOrderStatus.INITIATED)
//                .totalPrice(submitEvent.price() * submitEvent.quantity())
//                .totalDiscount(0L)
//                .build();
//            productOrderRepository.save(productOrder);
//            OrderLine orderLine = OrderLine.builder()
//                .productId(submitEvent.productId())
//                .productName(submitEvent.productName())
//                .price(submitEvent.price())
//                .thumbnailUrl(submitEvent.thumbnailUrl())
//                .quantity(submitEvent.quantity())
//                .discount(0L)
//                .orderLineStatus(OrderLineStatus.INITIATED)
//                .build();
//            productOrder.addOrderLine(orderLine);
//            orderLineRepository.save(orderLine);
//
//            OrderLineEvent orderLineEvent = OrderLineEvent.builder()
//                .orderLineId(orderLine.getId())
//                .productId(orderLine.getProductId())
//                .productName(orderLine.getProductName())
//                .price(orderLine.getPrice())
//                .discount(orderLine.getDiscount())
//                .quantity(orderLine.getQuantity())
//                .build();
//            ProductOrderEvent productOrderEvent = ProductOrderEvent.builder()
//                .productOrderId(productOrder.getId())
//                .orderLine(orderLineEvent)
//                .memberId(submitEvent.memberId())
//                .paymentMethodId(submitEvent.paymentMethodId())
//                .deliveryAddressId(submitEvent.deliveryAddressId())
//                .build();
//            if (kafkaHealthIndicator.isKafkaUp()) {
//                orderKafkaProducer.occurPaymentEvent(productOrderEvent);
//                // 장바구니 비우기
//                memberClient.clearCart(submitEvent.memberId(), List.of(submitEvent.productId()));
//
//                productOrder.finalizeOrder(
//                    ProductOrderStatus.COMPLETED,
//                    orderLine.getPrice() * orderLine.getQuantity(),
//                    orderLine.getDiscount()
//                );
//                productOrderRepository.save(productOrder);
//            } else {
//                log.error("Failed to send payment event");
//                orderKafkaProducer.occurPaymentFailure(productOrderEvent);
//            }
//
//        }
//    }
//
//}
