package com.payment.paymentcore.infrastructure.kafka;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payment.paymentcore.testConfig.IntegrationTestSupport;
import com.payment.paymentcore.infrastructure.kafka.event.EventResult;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

class PaymentKafkaProducerTest extends IntegrationTestSupport {

    @Autowired
    private PaymentKafkaProducer paymentKafkaProducer;
    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private CompletableFuture<SendResult<String, String>> future;

    @DisplayName("카프카로 결제 이벤트 발생")
    @Test
    void occur_payment_event() throws JsonProcessingException {
        // given
        EventResult eventResult = EventResult.builder().build();
        String topic = "payment_result";

        // when
        when(kafkaTemplate.send(eq(topic), anyString())).thenReturn(future);
        paymentKafkaProducer.occurPaymentEvent(eventResult);

        // then
        verify(kafkaTemplate, times(1)).send(eq(topic), anyString());
    }
}
