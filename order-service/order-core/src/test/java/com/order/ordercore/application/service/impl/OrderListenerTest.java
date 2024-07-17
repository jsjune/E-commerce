package com.order.ordercore.application.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order.ordercore.application.service.dto.DeliveryEvent;
import com.order.ordercore.application.service.dto.OrderOutBoxEvent;
import com.order.ordercore.application.service.dto.PaymentEvent;
import com.order.ordercore.application.service.dto.RollbackDeliveryEvent;
import com.order.ordercore.application.service.dto.RollbackPaymentEvent;
import com.order.ordercore.infrastructure.kafka.KafkaHealthIndicator;
import com.order.ordercore.infrastructure.kafka.OrderKafkaProducer;
import com.order.ordercore.infrastructure.kafka.event.EventResult;
import com.order.ordercore.infrastructure.kafka.event.ProductOrderEvent;
import com.order.ordercore.infrastructure.kafka.event.SubmitOrderEvent;
import com.order.ordercore.infrastructure.kafka.event.SubmitOrderEvents;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderListenerTest {

    @InjectMocks
    private OrderListener orderListener;
    @Mock
    private OrderKafkaProducer orderKafkaProducer;
    @Mock
    private KafkaHealthIndicator kafkaHealthIndicator;

    @Test
    @DisplayName("주문(상품) 이벤트를 받아서 카프카로 전송한다")
    void listenSubmitOrderFromProduct() throws JsonProcessingException {
        // given
        SubmitOrderEvent event = SubmitOrderEvent.builder().build();
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);

        // when
        orderListener.listenSubmitOrderFromProduct(event);

        // then
        verify(orderKafkaProducer, times(1)).occurSubmitOrderFromProductEvent(event);
    }

    @Test
    @DisplayName("주문(장바구니) 이벤트를 받아서 카프카로 전송한다")
    void listenSubmitOrderFromCarts() throws JsonProcessingException {
        // given
        SubmitOrderEvents event = SubmitOrderEvents.builder().build();
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);

        // when
        orderListener.listenSubmitOrderFromCarts(event);

        // then
        verify(orderKafkaProducer, times(1)).occurSubmitOrderFromCartEvent(event);
    }

    @Test
    @DisplayName("주문(결제) 이벤트를 받아서 카프카로 전송한다")
    void listenSubmitOrderToPayment() throws JsonProcessingException {
        // given
        ProductOrderEvent event = ProductOrderEvent.builder().build();

        // when
        orderListener.listenSubmitOrderToPayment(event);

        // then
        verify(orderKafkaProducer, times(1)).submitOrderComplete(event);
    }

    @Test
    @DisplayName("주문(결제 -> 배송) 이벤트를 받아서 카프카로 전송한다")
    void listenSubmitOrderFromPaymentToDelivery() throws JsonProcessingException {
        // given
        PaymentEvent event = new PaymentEvent(EventResult.builder().build());

        // when
        orderListener.listenSubmitOrderFromPaymentToDelivery(event);

        // then
        verify(orderKafkaProducer, times(1)).occurDeliveryEvent(event.eventResult());
    }

    @Test
    @DisplayName("주문(배송 -> 상품) 이벤트를 받아서 카프카로 전송한다")
    void listenSubmitOrderFromDeliveryToProduct() throws JsonProcessingException {
        // given
        DeliveryEvent event = new DeliveryEvent(EventResult.builder().build());

        // when
        orderListener.listenSubmitOrderFromDeliveryToProduct(event);

        // then
        verify(orderKafkaProducer, times(1)).occurProductEvent(event.eventResult());
    }

    @Test
    @DisplayName("주문(결제 롤백) 이벤트를 받아서 카프카로 전송한다")
    void listenRollbackPaymentEvent() throws JsonProcessingException {
        // given
        RollbackPaymentEvent event = new RollbackPaymentEvent(
            EventResult.builder().build());

        // when
        orderListener.listenRollbackPaymentEvent(event);

        // then
        verify(orderKafkaProducer, times(1)).occurRollbackPaymentEvent(event.eventResult());
    }

    @Test
    @DisplayName("주문(배송 롤백) 이벤트를 받아서 카프카로 전송한다")
    void listenRollbackDeliveryEvent() throws JsonProcessingException {
        // given
        RollbackDeliveryEvent event = new RollbackDeliveryEvent(
            EventResult.builder().build());

        // when
        orderListener.listenRollbackDeliveryEvent(event);

        // then
        verify(orderKafkaProducer, times(1)).occurRollbackDeliveryEvent(event.eventResult());
    }

    @Test
    @DisplayName("주문 아웃박스 이벤트를 받아서 카프카로 전송한다")
    void listenOrderOutBoxEvent() {
        // given
        OrderOutBoxEvent event = new OrderOutBoxEvent("topic", "message");

        // when
        orderListener.listenOrderOutBoxEvent(event);

        // then
        verify(orderKafkaProducer, times(1)).occurOutBoxEvent(event.topic(), event.message());
    }
}
