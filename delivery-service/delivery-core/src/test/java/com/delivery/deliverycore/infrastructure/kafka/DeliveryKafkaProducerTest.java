package com.delivery.deliverycore.infrastructure.kafka;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.delivery.deliverycore.infrastructure.kafka.event.EventResult;
import com.delivery.deliverycore.testConfig.IntegrationTestSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

class DeliveryKafkaProducerTest extends IntegrationTestSupport {
    @Autowired
    private DeliveryKafkaProducer deliveryKafkaProducer;
    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private CompletableFuture<SendResult<String, String>> future;

    @DisplayName("카프카로 배송 이벤트 발생")
    @Test
    void occur_delivery_event() throws JsonProcessingException {
        // given
        EventResult eventResult = EventResult.builder().build();
        String topic = "delivery_result";

        // when
        when(kafkaTemplate.send(eq(topic), anyString())).thenReturn(future);
        deliveryKafkaProducer.occurDeliveryEvent(eventResult);

        // then
        verify(kafkaTemplate, times(1)).send(eq(topic), anyString());
    }
}
