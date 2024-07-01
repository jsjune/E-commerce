package com.orderservice.usecase.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orderservice.entity.OrderLine;
import com.orderservice.repository.OrderLineRepository;
import com.orderservice.usecase.impl.OrderRollbackService;
import com.orderservice.usecase.kafka.OrderKafkaProducer;
import com.orderservice.usecase.kafka.event.EventResult;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderKafkaService {

    private final OrderLineRepository orderLineRepository;
    private final OrderKafkaProducer orderKafkaProducer;
    private final OrderRollbackService orderRollbackService;

    public void handleOrderFromPayment(EventResult eventResult)
        throws JsonProcessingException {
        Optional<OrderLine> findOrderLine = orderLineRepository.findById(
            eventResult.orderLine().orderLineId());
        if (findOrderLine.isPresent()) {
            OrderLine orderLine = findOrderLine.get();
            orderLine.assignPayment(eventResult.paymentId());
            orderLineRepository.save(orderLine);
            orderKafkaProducer.occurDeliveryEvent(eventResult);
        }
    }

    public void handleOrderFromDelivery(EventResult eventResult)
        throws JsonProcessingException {
        Optional<OrderLine> findOrderLine = orderLineRepository.findById(
            eventResult.orderLine().orderLineId());
        if (findOrderLine.isPresent()) {
            OrderLine orderLine = findOrderLine.get();
            orderLine.assignDelivery(eventResult.deliveryId());
            orderLineRepository.save(orderLine);
            orderKafkaProducer.occurProductEvent(eventResult);
        }
    }

    public void handleRollbackOrderFromPayment(EventResult eventResult) {
        orderRollbackService.rollbackOrder(eventResult.mapToOrderRollbackDto());
    }

    public void handleRollbackOrderFromDelivery(EventResult eventResult)
        throws JsonProcessingException {
        orderKafkaProducer.occurRollbackPaymentEvent(eventResult);
        orderRollbackService.rollbackOrder(eventResult.mapToOrderRollbackDto());
    }

    public void handleRollbackOrderFromProduct(EventResult eventResult)
        throws JsonProcessingException {
        orderKafkaProducer.occurRollbackDeliveryEvent(eventResult);
        orderKafkaProducer.occurRollbackPaymentEvent(eventResult);
        orderRollbackService.rollbackOrder(eventResult.mapToOrderRollbackDto());
    }

    public void handleRollbackOrderFailure(EventResult eventResult) throws JsonProcessingException {
        orderKafkaProducer.occurRollbackDeliveryFailure(eventResult);
        orderKafkaProducer.occurRollbackPaymentFailure(eventResult);
    }

    public void occurDeliveryFailure(EventResult eventResult) throws JsonProcessingException {
        orderKafkaProducer.occurDeliveryFailure(eventResult);
    }

    public void occurProductFailure(EventResult eventResult) throws JsonProcessingException {
        orderKafkaProducer.occurProductFailure(eventResult);
    }

    public void occurRollbackPaymentFailure(EventResult eventResult)
        throws JsonProcessingException {
        orderKafkaProducer.occurRollbackPaymentFailure(eventResult);
    }
}
