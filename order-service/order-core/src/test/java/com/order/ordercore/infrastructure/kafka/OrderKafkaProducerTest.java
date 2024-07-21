package com.order.ordercore.infrastructure.kafka;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order.ordercore.config.log.LoggingProducer;
import com.order.ordercore.testConfig.IntegrationTestSupport;
import com.order.ordercore.infrastructure.kafka.event.EventResult;
import com.order.ordercore.infrastructure.kafka.event.ProductOrderEvent;
import com.order.ordercore.infrastructure.kafka.event.SubmitOrderEvent;
import com.order.ordercore.infrastructure.kafka.event.SubmitOrderEvents;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

class OrderKafkaProducerTest extends IntegrationTestSupport {

    @Autowired
    private OrderKafkaProducer orderKafkaProducer;
    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private CompletableFuture<SendResult<String, String>> future;
    @MockBean
    private LoggingProducer loggingProducer;

    @Test
    @DisplayName("카프카로 배송 이벤트 발생")
    void occurDeliveryEvent() throws JsonProcessingException {
        // given
        EventResult event = EventResult.builder().build();
        String topic = "delivery_request";
        when(kafkaTemplate.send(eq(topic), anyString())).thenReturn(future);
        doNothing().when(loggingProducer).sendMessage(anyString(), anyString());

        // when
        orderKafkaProducer.occurDeliveryEvent(event);

        // then
        verify(kafkaTemplate).send(eq(topic), anyString());
    }

    @Test
    @DisplayName("카프카로 상품 이벤트 발생")
    void occurProductEvent() throws JsonProcessingException {
        // given
        EventResult event = EventResult.builder().build();
        String topic = "product_request";
        when(kafkaTemplate.send(eq(topic), anyString())).thenReturn(future);
        doNothing().when(loggingProducer).sendMessage(anyString(), anyString());

        // when
        orderKafkaProducer.occurProductEvent(event);

        // then
        verify(kafkaTemplate).send(eq(topic), anyString());
    }

    @Test
    @DisplayName("카프카로 결제 롤백 이벤트 발생")
    void occurRollbackPaymentEvent() throws JsonProcessingException {
        // given
        EventResult event = EventResult.builder().build();
        String topic = "payment_rollback_request";
        when(kafkaTemplate.send(eq(topic), anyString())).thenReturn(future);
        doNothing().when(loggingProducer).sendMessage(anyString(), anyString());

        // when
        orderKafkaProducer.occurRollbackPaymentEvent(event);

        // then
        verify(kafkaTemplate).send(eq(topic), anyString());
    }

    @Test
    @DisplayName("카프카로 배송 롤백 이벤트 발생")
    void occurRollbackDeliveryEvent() throws JsonProcessingException {
        // given
        EventResult event = EventResult.builder().build();
        String topic = "delivery_rollback_request";
        when(kafkaTemplate.send(eq(topic), anyString())).thenReturn(future);
        doNothing().when(loggingProducer).sendMessage(anyString(), anyString());

        // when
        orderKafkaProducer.occurRollbackDeliveryEvent(event);

        // then
        verify(kafkaTemplate).send(eq(topic), anyString());
    }

    @Test
    @DisplayName("카프카로 상품 주문 이벤트 발생")
    void occurSubmitOrderFromProductEvent() throws JsonProcessingException {
        // given
        SubmitOrderEvent event = SubmitOrderEvent.builder().build();
        String topic = "submit_order_product_request";
        when(kafkaTemplate.send(eq(topic), anyString())).thenReturn(future);
        doNothing().when(loggingProducer).sendMessage(anyString(), anyString());

        // when
        orderKafkaProducer.occurSubmitOrderFromProductEvent(event);

        // then
        verify(kafkaTemplate).send(eq(topic), anyString());
    }

    @Test
    @DisplayName("카프카로 장바구니 주문 이벤트 발생")
    void occurSubmitOrderFromCartEvent() throws JsonProcessingException {
        // given
        SubmitOrderEvents event = SubmitOrderEvents.builder().build();
        String topic = "submit_order_cart_request";
        when(kafkaTemplate.send(eq(topic), anyString())).thenReturn(future);
        doNothing().when(loggingProducer).sendMessage(anyString(), anyString());

        // when
        orderKafkaProducer.occurSubmitOrderFromCartEvent(event);

        // then
        verify(kafkaTemplate).send(eq(topic), anyString());
    }

    @Test
    @DisplayName("카프카로 결제 이벤트 발생")
    void submitOrderComplete() throws JsonProcessingException {
        // given
        ProductOrderEvent event = ProductOrderEvent.builder().build();
        String topic = "payment_request";
        when(kafkaTemplate.send(eq(topic), anyString())).thenReturn(future);
        doNothing().when(loggingProducer).sendMessage(anyString(), anyString());

        // when
        orderKafkaProducer.submitOrderComplete(event);

        // then
        verify(kafkaTemplate).send(eq(topic), anyString());
    }

    @Test
    @DisplayName("카프카로 아웃 박스 이벤트 발생")
    void occurOutBoxEvent() {
        // given
        String topic = "order_submit_request";
        String message = "message";
        when(kafkaTemplate.send(eq(topic), eq(message))).thenReturn(future);
        doNothing().when(loggingProducer).sendMessage(anyString(), anyString());

        // when
        orderKafkaProducer.occurOutBoxEvent(topic, message);

        // then
        verify(kafkaTemplate).send(eq(topic), eq(message));
    }
}
