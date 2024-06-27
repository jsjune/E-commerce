package com.orderservice.usecase.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.entity.OrderLine;
import com.orderservice.repository.OrderLineRepository;
import com.orderservice.usecase.impl.OrderRollbackService;
import com.orderservice.usecase.kafka.event.EventResult;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderKafkaProducer orderKafkaProducer;
    private final OrderRollbackService orderRollbackService;
    private final OrderLineRepository orderLineRepository;

    @KafkaListener(topics = "${consumers.topic1}", groupId = "${consumers.groupId}")
    public void consumeOrderFromPayment(ConsumerRecord<String, String> record)
        throws JsonProcessingException {
        EventResult eventResult = objectMapper.readValue(record.value(), EventResult.class);
        if (eventResult.status() == -1) {
            orderRollbackService.rollbackOrder(eventResult.mapToOrderRollbackDto());
        } else {
            Optional<OrderLine> findOrderLine = orderLineRepository.findById(
                eventResult.orderLine().orderLineId());
            if (findOrderLine.isPresent()) {
                OrderLine orderLine = findOrderLine.get();
                orderLine.assignPayment(eventResult.paymentId());
                orderLineRepository.save(orderLine);
                orderKafkaProducer.occurDeliveryEvent(eventResult);
            }
        }
    }

    @KafkaListener(topics = "${consumers.topic2}", groupId = "${consumers.groupId}")
    public void consumeOrderFromDelivery(ConsumerRecord<String, String> record)
        throws JsonProcessingException {
        EventResult eventResult = objectMapper.readValue(record.value(), EventResult.class);
        if (eventResult.status() == -1) {
            orderKafkaProducer.occurRollbackPaymentEvent(eventResult);
            orderRollbackService.rollbackOrder(eventResult.mapToOrderRollbackDto());
        } else {
            Optional<OrderLine> findOrderLine = orderLineRepository.findById(
                eventResult.orderLine().orderLineId());
            if (findOrderLine.isPresent()) {
                OrderLine orderLine = findOrderLine.get();
                orderLine.assignDelivery(eventResult.deliveryId());
                orderLineRepository.save(orderLine);
                orderKafkaProducer.occurProductEvent(eventResult);
            }
        }
    }

    @KafkaListener(topics = "${consumers.topic3}", groupId = "${consumers.groupId}")
    public void consumeOrderFromProduct(ConsumerRecord<String, String> record)
        throws JsonProcessingException {
        EventResult eventResult = objectMapper.readValue(record.value(), EventResult.class);
        if (eventResult.status() == -1) {
            orderKafkaProducer.occurRollbackDeliveryEvent(eventResult);
            orderKafkaProducer.occurRollbackPaymentEvent(eventResult);
            orderRollbackService.rollbackOrder(eventResult.mapToOrderRollbackDto());
        }
    }
}
