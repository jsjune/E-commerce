package com.order.orderconsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.orderconsumer.testConfig.IntegrationTestSupport;
import com.order.ordercore.application.service.dto.DeliveryEvent;
import com.order.ordercore.application.service.dto.PaymentEvent;
import com.order.ordercore.application.service.dto.RollbackDeliveryEvent;
import com.order.ordercore.application.service.dto.RollbackPaymentEvent;
import com.order.ordercore.infrastructure.entity.OrderLine;
import com.order.ordercore.infrastructure.entity.OrderLineStatus;
import com.order.ordercore.infrastructure.entity.ProductOrder;
import com.order.ordercore.infrastructure.entity.ProductOrderStatus;
import com.order.ordercore.infrastructure.kafka.KafkaHealthIndicator;
import com.order.ordercore.infrastructure.kafka.OrderKafkaProducer;
import com.order.ordercore.infrastructure.kafka.event.EventResult;
import com.order.ordercore.infrastructure.kafka.event.OrderLineEvent;
import com.order.ordercore.infrastructure.kafka.event.ProductInfoEvent;
import com.order.ordercore.infrastructure.kafka.event.ProductOrderEvent;
import com.order.ordercore.infrastructure.kafka.event.SubmitOrderEvent;
import com.order.ordercore.infrastructure.kafka.event.SubmitOrderEvents;
import com.order.ordercore.infrastructure.repository.OrderLineRepository;
import com.order.ordercore.infrastructure.repository.ProductOrderRepository;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class OrderEventConsumerTest extends IntegrationTestSupport {

    @Autowired
    private OrderEventConsumer orderEventConsumer;
    @MockBean
    private KafkaHealthIndicator kafkaHealthIndicator;
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private OrderLineRepository orderLineRepository;
    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private ApplicationEvents events;

    @BeforeEach
    void setUp() {
        orderLineRepository.deleteAllInBatch();
        productOrderRepository.deleteAllInBatch();
    }

    @DisplayName("submit_order_cart_request 토픽 받는 consumer, 장바구니에서 주문 생성")
    @Test
    void consumeOrderFromCartSubmit() throws JsonProcessingException {
        // given
        SubmitOrderEvents event = SubmitOrderEvents.builder()
            .memberId(1L)
            .paymentMethodId(1L)
            .deliveryAddressId(1L)
            .productInfo(List.of(ProductInfoEvent.builder()
                .productId(1L)
                .productName("상품")
                .price(2000L)
                .quantity(5L)
                .thumbnailUrl("url")
                .build()))
            .build();
        String json = objectMapper.writeValueAsString(event);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("submit_order_cart_request", 0, 0, null, json);

        // when
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);
        orderEventConsumer.consumeOrderFromCartSubmit(record);
        ProductOrder findProductOrder = productOrderRepository.findAll().stream().findFirst().get();

        // then
        long count = events.stream(ProductOrderEvent.class).count();
        assertEquals(count, 1);
        assertEquals(findProductOrder.getProductOrderStatus(), ProductOrderStatus.COMPLETED);
        assertEquals(findProductOrder.getTotalPrice(), event.productInfo().get(0).price()*event.productInfo().get(0).quantity());
        assertEquals(findProductOrder.getTotalDiscount(), 0);
        assertEquals(findProductOrder.getOrderLines().get(0).getOrderLineStatus(), OrderLineStatus.INITIATED);
        assertEquals(findProductOrder.getOrderLines().get(0).getProductId(), event.productInfo().get(0).productId());
        assertEquals(findProductOrder.getOrderLines().get(0).getProductName(), event.productInfo().get(0).productName());
    }

    @DisplayName("submit_order_product_request 토픽 받는 consumer, 상품에서 주문 생성")
    @Test
    void consumeOrderFromProductSubmit() throws JsonProcessingException {
        // given
        SubmitOrderEvent event = SubmitOrderEvent.builder()
            .memberId(1L)
            .paymentMethodId(1L)
            .deliveryAddressId(1L)
            .quantity(5L)
            .productId(1L)
            .productName("상품")
            .price(2000L)
            .thumbnailUrl("url")
            .build();
        String json = objectMapper.writeValueAsString(event);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("submit_order_product_request", 0, 0, null, json);

        // when
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);
        orderEventConsumer.consumeOrderFromProductSubmit(record);
        ProductOrder findProductOrder = productOrderRepository.findAll().stream().findFirst().get();

        // then
        long count = events.stream(ProductOrderEvent.class).count();
        assertEquals(count, 1);
        assertEquals(findProductOrder.getProductOrderStatus(), ProductOrderStatus.COMPLETED);
        assertEquals(findProductOrder.getTotalPrice(), event.price()*event.quantity());
        assertEquals(findProductOrder.getTotalDiscount(), 0);
        assertEquals(findProductOrder.getOrderLines().get(0).getOrderLineStatus(), OrderLineStatus.INITIATED);
        assertEquals(findProductOrder.getOrderLines().get(0).getProductId(), event.productId());
        assertEquals(findProductOrder.getOrderLines().get(0).getProductName(), event.productName());
    }

    @DisplayName("payment_result 토픽 받는 consumer, 결제 성공")
    @Test
    void consume_order_from_payment_pay_success() throws JsonProcessingException {
        // given
        ProductOrder productOrder = productOrderRepository.save(ProductOrder.builder().build());
        OrderLine orderLine = OrderLine.builder()
            .productOrder(productOrder)
            .orderLineStatus(OrderLineStatus.INITIATED)
            .build();
        productOrder.addOrderLine(orderLine);
        orderLineRepository.save(orderLine);
        OrderLineEvent orderLineEvent = OrderLineEvent.builder()
            .orderLineId(orderLine.getId())
            .productId(1L)
            .quantity(5L)
            .price(2000L)
            .discount(0L)
            .build();
        EventResult eventResult = EventResult.builder()
            .productOrderId(productOrder.getId())
            .orderLine(orderLineEvent)
            .memberId(1L)
            .paymentId(1L)
            .deliveryAddressId(1L)
            .paymentId(1L)
            .deliveryId(null)
            .status(1)
            .build();
        String json = objectMapper.writeValueAsString(eventResult);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("payment_result", 0, 0, null, json);
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);

        // when
        orderEventConsumer.consumeOrderFromPayment(record);
        ProductOrder findProductOrder = productOrderRepository.findAll().stream().findFirst().get();
        long count = events.stream(PaymentEvent.class).count();

        // then
        assertEquals(count, 1);
        assertEquals(findProductOrder.getOrderLines().get(0).getPaymentId(), eventResult.paymentId());
        assertEquals(findProductOrder.getOrderLines().get(0).getOrderLineStatus(), OrderLineStatus.PAYMENT_COMPLETED);

    }

    @DisplayName("payment_result 토픽 받는 consumer, 결제 실패 -> 롤백")
    @Test
    void consume_order_from_payment_pay_fail() throws JsonProcessingException {
        // given
        long price = 2000;
        long quantity = 5;
        long discount = 0;
        ProductOrder productOrder = ProductOrder.builder()
            .totalPrice(price * quantity)
            .totalDiscount(discount)
            .build();
        ProductOrder saveProductOrder = productOrderRepository.save(productOrder);
        OrderLine orderLine = OrderLine.builder()
            .productOrder(saveProductOrder)
            .orderLineStatus(OrderLineStatus.INITIATED)
            .build();
        productOrder.addOrderLine(orderLine);
        orderLineRepository.save(orderLine);
        OrderLineEvent orderLineEvent = OrderLineEvent.builder()
            .orderLineId(orderLine.getId())
            .productId(1L)
            .quantity(quantity)
            .price(price)
            .discount(discount)
            .build();
        EventResult eventResult = EventResult.builder()
            .productOrderId(productOrder.getId())
            .orderLine(orderLineEvent)
            .memberId(1L)
            .paymentId(1L)
            .deliveryAddressId(1L)
            .paymentId(1L)
            .deliveryId(null)
            .status(-1)
            .build();
        String json = objectMapper.writeValueAsString(eventResult);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("payment_result", 0, 0, null, json);

        // when
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);
        orderEventConsumer.consumeOrderFromPayment(record);
        ProductOrder findProductOrder = productOrderRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(findProductOrder.getOrderLines().get(0).getPaymentId(), eventResult.paymentId());
        assertEquals(findProductOrder.getOrderLines().get(0).getOrderLineStatus(), OrderLineStatus.CANCELLED);
        assertEquals(findProductOrder.getTotalPrice(), 0);
        assertEquals(findProductOrder.getTotalDiscount(), 0);

    }

    @DisplayName("delivery_result 토픽 받는 consumer, 배송 요청 성공")
    @Test
    void consumeOrderFromDelivery_success() throws JsonProcessingException {
        // given
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(1L)
            .productOrderStatus(ProductOrderStatus.COMPLETED)
            .totalPrice(10000L)
            .totalDiscount(0L)
            .build();
        productOrderRepository.save(productOrder);
        OrderLine orderLine = OrderLine.builder()
            .productOrder(productOrder)
            .orderLineStatus(OrderLineStatus.PAYMENT_COMPLETED)
            .productId(1L)
            .productName("상품")
            .quantity(5L)
            .price(2000L)
            .discount(0L)
            .paymentId(1L)
            .build();
        productOrder.addOrderLine(orderLine);
        orderLineRepository.save(orderLine);
        EventResult eventResult = EventResult.builder()
            .productOrderId(productOrder.getId())
            .orderLine(OrderLineEvent.builder()
                .orderLineId(orderLine.getId())
                .productId(orderLine.getProductId())
                .quantity(orderLine.getQuantity())
                .price(orderLine.getPrice())
                .discount(orderLine.getDiscount())
                .build())
            .memberId(productOrder.getMemberId())
            .paymentMethodId(1L)
            .deliveryAddressId(1L)
            .paymentId(orderLine.getPaymentId())
            .status(1)
            .build();
        String json = objectMapper.writeValueAsString(eventResult);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("delivery_result", 0, 0, null, json);
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);

        // when
        orderEventConsumer.consumeOrderFromDelivery(record);
        ProductOrder findProductOrder = productOrderRepository.findAll().stream().findFirst().get();
        long count = events.stream(DeliveryEvent.class).count();

        // then
        assertEquals(count, 1);
        assertEquals(findProductOrder.getOrderLines().get(0).getDeliveryId(), eventResult.deliveryId());
        assertEquals(findProductOrder.getOrderLines().get(0).getOrderLineStatus(), OrderLineStatus.DELIVERY_REQUESTED);
    }

    @DisplayName("delivery_result 토픽 받는 consumer, 배송 실패 -> 롤백")
    @Test
    void consumeOrderFromDelivery_fail() throws JsonProcessingException {
        // given
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(1L)
            .productOrderStatus(ProductOrderStatus.COMPLETED)
            .totalPrice(10000L)
            .totalDiscount(0L)
            .build();
        productOrderRepository.save(productOrder);
        OrderLine orderLine = OrderLine.builder()
            .productOrder(productOrder)
            .orderLineStatus(OrderLineStatus.PAYMENT_COMPLETED)
            .productId(1L)
            .productName("상품")
            .quantity(5L)
            .price(2000L)
            .discount(0L)
            .paymentId(1L)
            .build();
        productOrder.addOrderLine(orderLine);
        orderLineRepository.save(orderLine);
        EventResult eventResult = EventResult.builder()
            .productOrderId(productOrder.getId())
            .orderLine(OrderLineEvent.builder()
                .orderLineId(orderLine.getId())
                .productId(orderLine.getProductId())
                .quantity(orderLine.getQuantity())
                .price(orderLine.getPrice())
                .discount(orderLine.getDiscount())
                .build())
            .memberId(productOrder.getMemberId())
            .paymentMethodId(1L)
            .deliveryAddressId(1L)
            .paymentId(orderLine.getPaymentId())
            .status(-1)
            .build();
        String json = objectMapper.writeValueAsString(eventResult);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("delivery_result", 0, 0, null, json);
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);

        // when
        orderEventConsumer.consumeOrderFromDelivery(record);
        ProductOrder findProductOrder = productOrderRepository.findAll().stream().findFirst().get();
        long count = events.stream(RollbackPaymentEvent.class).count();

        // then
        assertEquals(count, 1);
        assertEquals(findProductOrder.getOrderLines().get(0).getDeliveryId(), eventResult.deliveryId());
        assertEquals(findProductOrder.getOrderLines().get(0).getOrderLineStatus(), OrderLineStatus.CANCELLED);
        assertEquals(findProductOrder.getTotalPrice(), 0);
        assertEquals(findProductOrder.getTotalDiscount(), 0);
    }

    @DisplayName("재고 부족으로 배송 결제 주문 롤백")
    @Test
    void consumeOrderFromProduct() throws JsonProcessingException {
        // given
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(1L)
            .productOrderStatus(ProductOrderStatus.COMPLETED)
            .totalPrice(10000L)
            .totalDiscount(0L)
            .build();
        productOrderRepository.save(productOrder);
        OrderLine orderLine = OrderLine.builder()
            .productOrder(productOrder)
            .orderLineStatus(OrderLineStatus.PAYMENT_COMPLETED)
            .productId(1L)
            .productName("상품")
            .quantity(5L)
            .price(2000L)
            .discount(0L)
            .paymentId(1L)
            .build();
        productOrder.addOrderLine(orderLine);
        orderLineRepository.save(orderLine);
        EventResult eventResult = EventResult.builder()
            .productOrderId(productOrder.getId())
            .orderLine(OrderLineEvent.builder()
                .orderLineId(orderLine.getId())
                .productId(orderLine.getProductId())
                .quantity(orderLine.getQuantity())
                .price(orderLine.getPrice())
                .discount(orderLine.getDiscount())
                .build())
            .memberId(productOrder.getMemberId())
            .paymentMethodId(1L)
            .deliveryAddressId(1L)
            .paymentId(orderLine.getPaymentId())
            .status(-1)
            .build();
        String json = objectMapper.writeValueAsString(eventResult);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("product_result", 0, 0, null, json);
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);

        // when
        orderEventConsumer.consumeOrderFromProduct(record);
        ProductOrder findProductOrder = productOrderRepository.findAll().stream().findFirst().get();
        long countDelivery = events.stream(RollbackDeliveryEvent.class).count();
        long countPayment = events.stream(RollbackPaymentEvent.class).count();

        // then
        assertEquals(countDelivery, 1);
        assertEquals(countPayment, 1);
        assertEquals(findProductOrder.getOrderLines().get(0).getPaymentId(), eventResult.paymentId());
        assertEquals(findProductOrder.getOrderLines().get(0).getDeliveryId(), eventResult.deliveryId());
        assertEquals(findProductOrder.getOrderLines().get(0).getOrderLineStatus(), OrderLineStatus.CANCELLED);
        assertEquals(findProductOrder.getTotalPrice(), 0);
        assertEquals(findProductOrder.getTotalDiscount(), 0);
    }
}
