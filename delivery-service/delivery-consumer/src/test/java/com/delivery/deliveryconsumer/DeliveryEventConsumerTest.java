package com.delivery.deliveryconsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.delivery.deliveryconsumer.testConfig.IntegrationTestSupport;
import com.deliveryservice.entity.Address;
import com.deliveryservice.entity.Delivery;
import com.deliveryservice.entity.DeliveryAddress;
import com.deliveryservice.entity.DeliveryStatus;
import com.deliveryservice.repository.DeliveryAddressRepository;
import com.deliveryservice.repository.DeliveryRepository;
import com.deliveryservice.usecase.DeliveryAddressUseCase;
import com.deliveryservice.usecase.dto.RegisterAddress;
import com.deliveryservice.usecase.kafka.DeliveryKafkaProducer;
import com.deliveryservice.usecase.kafka.KafkaHealthIndicator;
import com.deliveryservice.usecase.kafka.event.EventResult;
import com.deliveryservice.usecase.kafka.event.OrderLineEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class DeliveryEventConsumerTest extends IntegrationTestSupport {
    @Autowired
    private DeliveryEventConsumer deliveryEventConsumer;
    @MockBean
    private KafkaHealthIndicator kafkaHealthIndicator;
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private DeliveryRepository deliveryRepository;
    @MockBean
    private DeliveryKafkaProducer deliveryKafkaProducer;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private DeliveryAddressUseCase deliveryAddressUseCase;

    @BeforeEach
    void setUp() {
        deliveryAddressRepository.deleteAllInBatch();
        deliveryRepository.deleteAllInBatch();
    }

    @DisplayName("delivery_request 토픽을 하는 consumer, 배송 요청")
    @Test
    void consumeDelivery() throws Exception {
        // given
        long memberId = 1L;
        boolean mainAddress = true;
        RegisterAddress command = getAddressRequest(mainAddress);
        deliveryAddressUseCase.registerAddress(memberId, command);
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findAll().stream().findFirst()
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
            .deliveryAddressId(deliveryAddress.getId())
            .paymentId(1L)
            .deliveryId(null)
            .build();
        String json = objectMapper.writeValueAsString(eventResult);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("delivery_request", 0, 0, null, json);

        // when
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);
        deliveryEventConsumer.consumeDelivery(record);
        Delivery result = deliveryRepository.findAll().stream().findFirst().get();

        // then
        verify(deliveryKafkaProducer, times(1)).occurDeliveryEvent(any());
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

    private static RegisterAddress getAddressRequest(boolean mainAddress) {
        return RegisterAddress.builder()
            .street("서울시 강남구")
            .detailAddress("역삼동")
            .zipCode("12345")
            .alias("집")
            .receiver("홍길동")
            .mainAddress(mainAddress)
            .build();
    }
}
