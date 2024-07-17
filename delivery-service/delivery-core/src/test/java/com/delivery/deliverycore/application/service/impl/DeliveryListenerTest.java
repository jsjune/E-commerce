package com.delivery.deliverycore.application.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.delivery.deliverycore.infrastructure.kafka.DeliveryKafkaProducer;
import com.delivery.deliverycore.infrastructure.kafka.event.EventResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryListenerTest {

    @InjectMocks
    private DeliveryListener deliveryListener;
    @Mock
    private DeliveryKafkaProducer deliveryKafkaProducer;

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
