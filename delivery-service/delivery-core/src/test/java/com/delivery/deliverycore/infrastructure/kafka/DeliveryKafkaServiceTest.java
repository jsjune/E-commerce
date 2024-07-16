package com.delivery.deliverycore.infrastructure.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.delivery.deliverycore.application.service.DeliveryProcessUseCase;
import com.delivery.deliverycore.infrastructure.entity.Address;
import com.delivery.deliverycore.infrastructure.entity.Delivery;
import com.delivery.deliverycore.infrastructure.entity.DeliveryAddress;
import com.delivery.deliverycore.infrastructure.entity.DeliveryOutBox;
import com.delivery.deliverycore.infrastructure.kafka.event.EventResult;
import com.delivery.deliverycore.infrastructure.kafka.event.OrderLineEvent;
import com.delivery.deliverycore.infrastructure.repository.DeliveryAddressRepository;
import com.delivery.deliverycore.infrastructure.repository.DeliveryOutBoxRepository;
import com.delivery.deliverycore.infrastructure.repository.DeliveryRepository;
import com.delivery.deliverycore.testConfig.IntegrationTestSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class DeliveryKafkaServiceTest extends IntegrationTestSupport {
    @Autowired
    private DeliveryKafkaService deliveryKafkaService;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private ApplicationEvents events;
    @MockBean
    private DeliveryProcessUseCase deliveryProcessUseCase;
    @Autowired
    private DeliveryOutBoxRepository deliveryOutBoxRepository;

    @BeforeEach
    void setUp() {
        deliveryOutBoxRepository.deleteAllInBatch();
        deliveryAddressRepository.deleteAllInBatch();
        deliveryRepository.deleteAllInBatch();
    }

    @DisplayName("배송 이벤트 처리")
    @Test
    void handle_delivery() throws Exception {
        // given
        // given
        DeliveryAddress deliveryAddress = DeliveryAddress.builder().address(Address.builder().build()).build();
        deliveryAddressRepository.save(deliveryAddress);
        Delivery delivery = Delivery.builder()
            .deliveryAddress(deliveryAddress)
            .build();
        deliveryRepository.save(delivery);
        EventResult eventResult = EventResult.builder()
            .orderLine(OrderLineEvent.builder().build())
            .deliveryAddressId(deliveryAddress.getId())
            .build();

        // when
        when(deliveryProcessUseCase.processDelivery(eventResult.mapToCommand())).thenReturn(delivery.getId());
        deliveryKafkaService.handleDelivery(eventResult);
        long count = events.stream(EventResult.class).count();

        // then
        verify(deliveryProcessUseCase, times(1)).processDelivery(eventResult.mapToCommand());
        assertEquals(count, 1);

    }

    @DisplayName("카프카 네트워크 오류로 이벤트를 outbox에 저장")
    @Test
    void occur_delivery_failure() throws JsonProcessingException {
        // given
        EventResult eventResult = EventResult.builder()
            .orderLine(OrderLineEvent.builder().build())
            .build();

        // when
        deliveryKafkaService.occurDeliveryFailure(eventResult);
        DeliveryOutBox result = deliveryOutBoxRepository.findAll().stream().findFirst()
            .get();

        // then
        assertEquals(result.getTopic(), "delivery_result");
        assertFalse(result.isSuccess());

    }
}
