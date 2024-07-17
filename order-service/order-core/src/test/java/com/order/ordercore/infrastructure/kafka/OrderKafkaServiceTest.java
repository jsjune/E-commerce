package com.order.ordercore.infrastructure.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order.ordercore.testConfig.IntegrationTestSupport;
import com.order.ordercore.application.service.dto.DeliveryEvent;
import com.order.ordercore.application.service.dto.OrderOutBoxEvent;
import com.order.ordercore.application.service.dto.OrderRollbackDto;
import com.order.ordercore.application.service.dto.PaymentEvent;
import com.order.ordercore.application.service.dto.RollbackDeliveryEvent;
import com.order.ordercore.application.service.dto.RollbackPaymentEvent;
import com.order.ordercore.application.service.impl.OrderRollbackService;
import com.order.ordercore.infrastructure.entity.OrderLine;
import com.order.ordercore.infrastructure.entity.OrderOutBox;
import com.order.ordercore.infrastructure.entity.ProductOrder;
import com.order.ordercore.infrastructure.entity.ProductOrderStatus;
import com.order.ordercore.infrastructure.kafka.event.EventResult;
import com.order.ordercore.infrastructure.kafka.event.OrderLineEvent;
import com.order.ordercore.infrastructure.kafka.event.ProductInfoEvent;
import com.order.ordercore.infrastructure.kafka.event.ProductOrderEvent;
import com.order.ordercore.infrastructure.kafka.event.SubmitOrderEvent;
import com.order.ordercore.infrastructure.kafka.event.SubmitOrderEvents;
import com.order.ordercore.infrastructure.repository.OrderLineRepository;
import com.order.ordercore.infrastructure.repository.OrderOutBoxRepository;
import com.order.ordercore.infrastructure.repository.ProductOrderRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class OrderKafkaServiceTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationEvents events;
    @Autowired
    private OrderKafkaService orderKafkaService;
    @Autowired
    private OrderLineRepository orderLineRepository;
    @Autowired
    private ProductOrderRepository productOrderRepository;
    @MockBean
    private OrderRollbackService orderRollbackService;
    @Autowired
    private OrderOutBoxRepository outBoxRepository;
    @MockBean
    private KafkaHealthIndicator kafkaHealthIndicator;

    @BeforeEach
    void setUp() {
        outBoxRepository.deleteAllInBatch();
        orderLineRepository.deleteAllInBatch();
        productOrderRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("컨슈머에서 결제 이벤트를 받아서 카프카 전송을 위해 비동기로 이벤트로 전송")
    void handleOrderFromPayment() {
        // given
        long paymentId = 1L;
        OrderLine orderLine = OrderLine.builder().build();
        orderLineRepository.save(orderLine);
        EventResult event = EventResult.builder()
            .orderLine(OrderLineEvent.builder().orderLineId(orderLine.getId()).build())
            .paymentId(paymentId)
            .build();

        // when
        orderKafkaService.handleOrderFromPayment(event);
        OrderLine result = orderLineRepository.findAll().stream().findFirst().get();
        long count = events.stream(PaymentEvent.class).count();

        // then
        assertEquals(result.getPaymentId(), paymentId);
        assertEquals(count, 1);
    }

    @Test
    @DisplayName("컨슈머에서 배송 이벤트를 받아서 카프카 전송을 위해 비동기로 이벤트로 전송")
    void handleOrderFromDelivery() {
        // given
        long deliveryId = 1L;
        OrderLine orderLine = OrderLine.builder().build();
        orderLineRepository.save(orderLine);
        EventResult event = EventResult.builder()
            .orderLine(OrderLineEvent.builder().orderLineId(orderLine.getId()).build())
            .deliveryId(deliveryId)
            .build();

        // when
        orderKafkaService.handleOrderFromDelivery(event);
        OrderLine result = orderLineRepository.findAll().stream().findFirst().get();
        long count = events.stream(DeliveryEvent.class).count();

        // then
        assertEquals(result.getDeliveryId(), deliveryId);
        assertEquals(count, 1);
    }

    @Test
    @DisplayName("컨슈머에서 결제로부터 이벤트를 받고 주문 롤백")
    void handleRollbackOrderFromPayment() {
        // given
        EventResult event = EventResult.builder()
            .orderLine(OrderLineEvent.builder()
                .price(1000L)
                .quantity(3L)
                .build())
            .build();

        // when
        orderKafkaService.handleRollbackOrderFromPayment(event);

        // then
        verify(orderRollbackService, times(1)).rollbackOrder(event.mapToOrderRollbackDto());
    }

    @Test
    @DisplayName("컨슈머에서 배달로부터 이벤트를 받고 주문 롤백")
    void handleRollbackOrderFromDelivery() {
        // given
        EventResult event = EventResult.builder()
            .orderLine(OrderLineEvent.builder()
                .price(1000L)
                .quantity(3L)
                .build())
            .build();

        // when
        orderKafkaService.handleRollbackOrderFromDelivery(event);
        long count = events.stream(RollbackPaymentEvent.class).count();

        // then
        assertEquals(count, 1);
        verify(orderRollbackService, times(1)).rollbackOrder(event.mapToOrderRollbackDto());
    }

    @Test
    @DisplayName("컨슈머에서 상품으로부터 이벤트를 받고 주문 롤백")
    void handleRollbackOrderFromProduct() {
        // given
        EventResult event = EventResult.builder()
            .orderLine(OrderLineEvent.builder()
                .price(1000L)
                .quantity(3L)
                .build())
            .build();

        // when
        orderKafkaService.handleRollbackOrderFromProduct(event);
        long paymentCount = events.stream(RollbackPaymentEvent.class).count();
        long deliveryCount = events.stream(RollbackDeliveryEvent.class).count();

        // then
        assertEquals(paymentCount, 1);
        assertEquals(deliveryCount, 1);
        verify(orderRollbackService, times(1)).rollbackOrder(event.mapToOrderRollbackDto());
    }

    @Test
    @DisplayName("카프카 네트워크 오류로 배송 아웃 박스 이벤트 저장")
    void occurDeliveryFailure() throws JsonProcessingException {
        // given
        EventResult event = EventResult.builder().build();
        String topic = "delivery_request";

        // when
        orderKafkaService.occurDeliveryFailure(event);
        OrderOutBox result = outBoxRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getTopic(), topic);
    }

    @Test
    @DisplayName("카프카 네트워크 오류로 상품 아웃 박스 이벤트 저장")
    void occurProductFailure() throws JsonProcessingException {
        // given
        EventResult event = EventResult.builder().build();
        String topic = "product_request";

        // when
        orderKafkaService.occurProductFailure(event);
        OrderOutBox result = outBoxRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getTopic(), topic);
    }

    @Test
    @DisplayName("카프카 네트워크 오류로 결제 롤백 아웃 박스 이벤트 저장")
    void occurRollbackPaymentFailure() throws JsonProcessingException {
        // given
        EventResult event = EventResult.builder().build();
        String topic = "payment_rollback_request";

        // when
        orderKafkaService.occurRollbackPaymentFailure(event);
        OrderOutBox result = outBoxRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getTopic(), topic);
    }

    @Test
    @DisplayName("카프카 네트워크 오류로 배송 롤백 아웃 박스 이벤트 저장")
    void occurRollbackDeliveryFailure() throws JsonProcessingException {
        // given
        EventResult event = EventResult.builder().build();
        String topic = "delivery_rollback_request";

        // when
        orderKafkaService.occurRollbackDeliveryFailure(event);
        OrderOutBox result = outBoxRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getTopic(), topic);
    }

    @Test
    @DisplayName("카프카 네트워크 오류로 결제, 배송 롤백 아웃 박스 이벤트 저장")
    void handleRollbackOrderFailure() throws JsonProcessingException {
        // given
        EventResult event = EventResult.builder().build();
        String topic1 = "payment_rollback_request";
        String topic2 = "delivery_rollback_request";

        // when
        orderKafkaService.handleRollbackOrderFailure(event);
        List<OrderOutBox> result = outBoxRepository.findAll();

        // then
        assertEquals(result.get(0).getTopic(), topic1);
        assertEquals(result.get(1).getTopic(), topic2);
    }

    @Test
    @DisplayName("카프카 네트워크 오류로 결제 아웃 박스 이벤트 저장")
    void occurPaymentFailure() throws JsonProcessingException {
        // given
        ProductOrderEvent event = ProductOrderEvent.builder().build();
        String topic = "payment_request";

        // when
        orderKafkaService.occurPaymentFailure(event);
        OrderOutBox result = outBoxRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getTopic(), topic);
    }

    @Test
    @DisplayName("카프카 네트워크 오류로 상품 주문 아웃 박스 이벤트 저장")
    void occurSubmitOrderFromProductEventFailure() throws JsonProcessingException {
        // given
        SubmitOrderEvent event = SubmitOrderEvent.builder().build();
        String topic = "submit_order_product_request";

        // when
        orderKafkaService.occurSubmitOrderFromProductEventFailure(event);
        OrderOutBox result = outBoxRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getTopic(), topic);
    }

    @Test
    @DisplayName("카프카 네트워크 오류로 장바구니 주문 아웃 박스 이벤트 저장")
    void occurSubmitOrderFromCartEventFailure() throws JsonProcessingException {
        // given
        SubmitOrderEvents event = SubmitOrderEvents.builder().build();
        String topic = "submit_order_cart_request";

        // when
        orderKafkaService.occurSubmitOrderFromCartEventFailure(event);
        OrderOutBox result = outBoxRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getTopic(), topic);
    }

    @Test
    @DisplayName("outBox에 저장되어 있는 payment 토픽 이벤트 처리")
    void processOutboxMessage_payment() throws JsonProcessingException {
        // given
        ProductOrder productOrder = ProductOrder.builder().build();
        productOrderRepository.save(productOrder);
        long price = 1000L;
        long quantity = 3L;
        OrderLine orderLine = OrderLine.builder()
            .price(price)
            .quantity(quantity)
            .discount(0L)
            .build();
        productOrder.addOrderLine(orderLine);
        orderLineRepository.save(orderLine);

        String topic = "payment_request";
        String message = String.format(
            "{\"productOrderId\":%d,\"orderLine\":null,\"memberId\":null,\"paymentMethodId\":null,\"deliveryAddressId\":null}",
            productOrder.getId());
        OrderOutBox outBox = OrderOutBox.builder()
            .message(message)
            .topic(topic)
            .success(false)
            .build();

        // when
        orderKafkaService.processOutboxMessage(outBox);
        ProductOrder result = productOrderRepository.findAll().stream().findFirst().get();
        long count = events.stream(OrderOutBoxEvent.class).count();

        // then
        assertEquals(count, 1);
        assertEquals(result.getProductOrderStatus(), ProductOrderStatus.COMPLETED);
        assertEquals(result.getTotalPrice(), price * quantity);
    }

    @DisplayName("outBox에 저장되어 있는 delivery 토픽 이벤트 처리")
    @Test
    void processOutboxMessage_delivery() throws JsonProcessingException {
        // given
        ProductOrder productOrder = ProductOrder.builder().build();
        productOrderRepository.save(productOrder);
        long price = 1000L;
        long quantity = 3L;
        OrderLine orderLine = OrderLine.builder()
            .price(price)
            .quantity(quantity)
            .discount(0L)
            .build();
        productOrder.addOrderLine(orderLine);
        orderLineRepository.save(orderLine);

        String topic = "delivery_request";
        Long paymentId = 1L;
        String message = String.format(
            "{\"productOrderId\":%d,\"orderLine\":{\"orderLineId\":%d,\"productId\":null,\"productName\":null,\"price\":null,\"discount\":null,\"quantity\":null},\"memberId\":null,\"paymentMethodId\":null,\"deliveryAddressId\":null,\"paymentId\":%d,\"deliveryId\":null,\"status\":0}",
            productOrder.getId(), orderLine.getId(), paymentId);
        OrderOutBox outBox = OrderOutBox.builder()
            .message(message)
            .topic(topic)
            .success(false)
            .build();

        // when
        orderKafkaService.processOutboxMessage(outBox);
        OrderLine result = orderLineRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getPaymentId(), paymentId);

    }

    @DisplayName("outBox에 저장되어 있는 product 토픽 이벤트 처리")
    @Test
    void processOutboxMessage_product() throws JsonProcessingException {
        // given
        ProductOrder productOrder = ProductOrder.builder().build();
        productOrderRepository.save(productOrder);
        long price = 1000L;
        long quantity = 3L;
        OrderLine orderLine = OrderLine.builder()
            .price(price)
            .quantity(quantity)
            .discount(0L)
            .build();
        productOrder.addOrderLine(orderLine);
        orderLineRepository.save(orderLine);

        String topic = "product_request";
        Long deliveryId = 1L;
        String message = String.format(
            "{\"productOrderId\":%d,\"orderLine\":{\"orderLineId\":%d,\"productId\":null,\"productName\":null,\"price\":null,\"discount\":null,\"quantity\":null},\"memberId\":null,\"paymentMethodId\":null,\"deliveryAddressId\":null,\"paymentId\":null,\"deliveryId\":%d,\"status\":0}",
            productOrder.getId(), orderLine.getId(), deliveryId);
        OrderOutBox outBox = OrderOutBox.builder()
            .message(message)
            .topic(topic)
            .success(false)
            .build();

        // when
        orderKafkaService.processOutboxMessage(outBox);
        OrderLine result = orderLineRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getDeliveryId(), deliveryId);

    }

    @DisplayName("outBox에 저장되어 있는 rollback payment 토픽 이벤트 처리")
    @Test
    void processOutboxMessage_rollback_payment() throws JsonProcessingException {
        // given
        String topic = "payment_rollback_request";
        Long price = 1000L;
        Long quantity = 3L;
        String message = String.format(
            "{\"productOrderId\":null,\"orderLine\":{\"orderLineId\":null,\"productId\":null,\"productName\":null,\"price\":%d,\"discount\":null,\"quantity\":%d},\"memberId\":null,\"paymentMethodId\":null,\"deliveryAddressId\":null,\"paymentId\":null,\"deliveryId\":null,\"status\":0}",price, quantity);
        OrderOutBox outBox = OrderOutBox.builder()
            .topic(topic)
            .message(message)
            .build();

        // when
        orderKafkaService.processOutboxMessage(outBox);

        // then
        verify(orderRollbackService, times(1)).rollbackOrder(any(OrderRollbackDto.class));
    }

    @DisplayName("outBox에 저장되어 있는 rollback delivery 토픽 이벤트 처리")
    @Test
    void processOutboxMessage_rollback_delivery() throws JsonProcessingException {
        // given
        String topic = "delivery_rollback_request";
        Long price = 1000L;
        Long quantity = 3L;
        String message = String.format(
            "{\"productOrderId\":null,\"orderLine\":{\"orderLineId\":null,\"productId\":null,\"productName\":null,\"price\":%d,\"discount\":null,\"quantity\":%d},\"memberId\":null,\"paymentMethodId\":null,\"deliveryAddressId\":null,\"paymentId\":null,\"deliveryId\":null,\"status\":0}",price, quantity);
        OrderOutBox outBox = OrderOutBox.builder()
            .topic(topic)
            .message(message)
            .build();

        // when
        orderKafkaService.processOutboxMessage(outBox);

        // then
        verify(orderRollbackService, times(1)).rollbackOrder(any(OrderRollbackDto.class));
    }

    @Test
    @DisplayName("submit_order_product_request 이벤트 처리")
    void submitOrderFromProduct() throws JsonProcessingException {
        // given
        SubmitOrderEvent event = SubmitOrderEvent.builder()
            .price(1000L)
            .quantity(3L)
            .build();
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);

        // when
        orderKafkaService.submitOrderFromProduct(event);
        ProductOrder result = productOrderRepository.findAll().stream().findFirst().get();
        long count = events.stream(ProductOrderEvent.class).count();

        // then
        assertEquals(count, 1);
        assertNotNull(result);
        assertNotNull(result.getOrderLines());
        assertEquals(result.getProductOrderStatus(), ProductOrderStatus.COMPLETED);
        assertEquals(result.getTotalPrice(), event.price() * event.quantity());
    }

    @Test
    @DisplayName("submit_order_cart_request 이벤트 처리")
    void submitOrderFromCart() throws JsonProcessingException {
        // given
        SubmitOrderEvents event = SubmitOrderEvents.builder()
            .productInfo(List.of(ProductInfoEvent.builder()
                .price(1000L)
                .quantity(3L)
                .build()))
            .build();
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);

        // when
        orderKafkaService.submitOrderFromCart(event);
        ProductOrder result = productOrderRepository.findAll().stream().findFirst().get();
        long count = events.stream(ProductOrderEvent.class).count();

        // then
        assertEquals(count, 1);
        assertEquals(result.getProductOrderStatus(), ProductOrderStatus.COMPLETED);
    }
}
