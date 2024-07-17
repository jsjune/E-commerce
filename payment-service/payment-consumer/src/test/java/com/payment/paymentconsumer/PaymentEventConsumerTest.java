package com.payment.paymentconsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.paymentconsumer.testConfig.IntegrationTestSupport;
import com.payment.paymentcore.infrastructure.entity.Payment;
import com.payment.paymentcore.infrastructure.entity.PaymentMethod;
import com.payment.paymentcore.infrastructure.entity.PaymentStatus;
import com.payment.paymentcore.infrastructure.entity.Refund;
import com.payment.paymentcore.infrastructure.kafka.KafkaHealthIndicator;
import com.payment.paymentcore.infrastructure.kafka.PaymentKafkaProducer;
import com.payment.paymentcore.infrastructure.kafka.event.EventResult;
import com.payment.paymentcore.infrastructure.kafka.event.OrderLineEvent;
import com.payment.paymentcore.infrastructure.kafka.event.ProductOrderEvent;
import com.payment.paymentcore.infrastructure.repository.PaymentMethodRepository;
import com.payment.paymentcore.infrastructure.repository.PaymentRepository;
import com.payment.paymentcore.infrastructure.repository.RefundRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
public class PaymentEventConsumerTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationEvents events;
    @Autowired
    private PaymentEventConsumer paymentEventConsumer;
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    private KafkaHealthIndicator kafkaHealthIndicator;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private RefundRepository refundRepository;

    @DisplayName("payment_request 토픽 받는 consumer, 결제 성공")
    @Test
    void consumePayment() throws JsonProcessingException {
        // given
        PaymentMethod paymentMethod = PaymentMethod.builder().build();
        paymentMethodRepository.save(paymentMethod);
        ProductOrderEvent event = ProductOrderEvent.builder()
            .productOrderId(1L)
            .orderLine(OrderLineEvent.builder()
                .orderLineId(1L)
                .productId(1L)
                .productName("상품")
                .price(1000L)
                .quantity(3L)
                .discount(0L)
                .build())
            .memberId(1L)
            .paymentMethodId(paymentMethod.getId())
            .deliveryAddressId(1L)
            .build();
        String json = objectMapper.writeValueAsString(event);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("payment_request", 0, 0, null,
            json);
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);

        // when
        paymentEventConsumer.consumePayment(record);
        Payment result = paymentRepository.findAll().stream().findFirst().get();
        long count = events.stream(EventResult.class).count();

        // then
        assertEquals(count, 1);
        assertEquals(result.getTotalPrice(),
            event.orderLine().price() * event.orderLine().quantity());
        assertEquals(result.getDiscountPrice(), event.orderLine().discount());
    }

    @DisplayName("payment_rollback_request 토픽 받는 consumer, 결제 롤백")
    @Test
    void consumeRollbackPayment() throws Exception {
        // given
        PaymentMethod paymentMethod = PaymentMethod.builder().build();
        paymentMethodRepository.save(paymentMethod);
        Payment payment = Payment.builder()
            .paymentMethod(paymentMethod)
            .totalPrice(10000L)
            .discountPrice(0L)
            .paymentStatus(PaymentStatus.COMPLETED)
            .referenceCode("1234")
            .build();
        paymentRepository.save(payment);
        EventResult event = EventResult.builder()
            .paymentMethodId(paymentMethod.getId())
            .orderLine(OrderLineEvent.builder()
                .price(2000L)
                .quantity(5L)
                .discount(0L)
                .build())
            .paymentId(payment.getId())
            .build();
        String json = objectMapper.writeValueAsString(event);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("payment_rollback_request", 0,
            0, null, json);

        // when
        paymentEventConsumer.consumeRollbackPayment(record);
        Payment savedPayment = paymentRepository.findAll().stream().findFirst().get();
        Refund result = refundRepository.findAll().stream().findFirst().get();

        // then
        assertNotNull(result);
        assertEquals(savedPayment.getPaymentStatus(), PaymentStatus.CANCELED);
    }
}
