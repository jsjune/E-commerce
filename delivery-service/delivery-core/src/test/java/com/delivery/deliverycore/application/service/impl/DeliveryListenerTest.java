package com.delivery.deliverycore.application.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.delivery.deliverycore.infrastructure.kafka.DeliveryKafkaProducer;
import com.delivery.deliverycore.infrastructure.kafka.event.EventResult;
import com.delivery.deliverycore.testConfig.IntegrationTestSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class DeliveryListenerTest extends IntegrationTestSupport {

    @MockBean
    private DeliveryKafkaProducer deliveryKafkaProducer;
    @Autowired
    private DeliveryListener deliveryListener;

    @DisplayName("delivery event 발생 시 kafka로 전달")
    @Test
    void listen_delivery_event() throws JsonProcessingException {
        // given
        EventResult eventResult = EventResult.builder().build();

        // when
        deliveryListener.listenDeliveryEvent(eventResult);

        // then
        verify(deliveryKafkaProducer, times(1)).occurDeliveryEvent(eventResult);

    }
}
