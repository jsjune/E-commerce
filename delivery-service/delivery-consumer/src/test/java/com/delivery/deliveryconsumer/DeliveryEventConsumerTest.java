package com.delivery.deliveryconsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.delivery.deliveryconsumer.testConfig.IntegrationTestSupport;
import com.delivery.deliverycore.infrastructure.entity.Address;
import com.delivery.deliverycore.infrastructure.entity.Delivery;
import com.delivery.deliverycore.infrastructure.entity.DeliveryAddress;
import com.delivery.deliverycore.infrastructure.entity.DeliveryStatus;
import com.delivery.deliverycore.infrastructure.kafka.KafkaHealthIndicator;
import com.delivery.deliverycore.infrastructure.kafka.event.EventResult;
import com.delivery.deliverycore.infrastructure.kafka.event.OrderLineEvent;
import com.delivery.deliverycore.infrastructure.repository.DeliveryAddressRepository;
import com.delivery.deliverycore.infrastructure.repository.DeliveryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
public class DeliveryEventConsumerTest extends IntegrationTestSupport {
    @Autowired
    private DeliveryEventConsumer deliveryEventConsumer;
    @MockBean
    private KafkaHealthIndicator kafkaHealthIndicator;
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private ApplicationEvents events;

    @BeforeEach
    void setUp() {
        deliveryAddressRepository.deleteAllInBatch();
        deliveryRepository.deleteAllInBatch();
    }

    @DisplayName("delivery_request 토픽을 하는 consumer, 배송 요청")
    @Test
    void consumeDelivery() throws Exception {
        // given
        DeliveryAddress deliveryAddress = DeliveryAddress.builder().address(Address.builder().build()).build();
        deliveryAddressRepository.save(deliveryAddress);
        DeliveryAddress findDeliveryAddress = deliveryAddressRepository.findAll().stream().findFirst()
            .get();
        EventResult eventResult = EventResult.builder()
            .productOrderId(1L)
            .orderLine(OrderLineEvent.builder()
                .orderLineId(1L)
                .productId(1L)
                .productName("상품")
                .price(1000L)
                .quantity(3L)
                .build())
            .memberId(1L)
            .paymentMethodId(1L)
            .deliveryAddressId(findDeliveryAddress.getId())
            .paymentId(1L)
            .deliveryId(null)
            .build();
        String json = objectMapper.writeValueAsString(eventResult);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("delivery_request", 0, 0, null, json);

        // when
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);
        deliveryEventConsumer.consumeDelivery(record);
        Delivery result = deliveryRepository.findAll().stream().findFirst().get();
        long count = events.stream(EventResult.class).count();

        // then
        assertEquals(count, 1);
        assertEquals(result.getProductId(), eventResult.orderLine().productId());
        assertEquals(result.getStatus(), DeliveryStatus.REQUESTED);
    }

    @DisplayName("delivery_rollback_request 토픽을 하는 consumer, 배송 롤백 요청")
    @Test
    void test() throws Exception {
        // given
        DeliveryAddress deliveryAddress = DeliveryAddress.builder().address(Address.builder().build()).build();
        deliveryAddressRepository.save(deliveryAddress);
        Delivery delivery = Delivery.builder().deliveryAddress(deliveryAddress).build();
        deliveryRepository.save(delivery);
        EventResult eventResult = EventResult.builder()
            .deliveryId(delivery.getId())
            .build();
        String json = objectMapper.writeValueAsString(eventResult);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("delivery_rollback_request", 0, 0, null, json);

        // when
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);
        deliveryEventConsumer.consumeRollbackDelivery(record);

        // then
        assertEquals(delivery.getStatus(), DeliveryStatus.CANCELED);
        assertNotNull(delivery.getReferenceCode());
    }

}
