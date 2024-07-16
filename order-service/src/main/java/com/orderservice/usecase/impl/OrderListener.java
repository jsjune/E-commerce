package com.orderservice.usecase.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orderservice.usecase.dto.DeliveryEvent;
import com.orderservice.usecase.dto.OrderOutBoxEvent;
import com.orderservice.usecase.dto.PaymentEvent;
import com.orderservice.usecase.dto.RollbackDeliveryEvent;
import com.orderservice.usecase.dto.RollbackPaymentEvent;
import com.orderservice.infrastructure.kafka.KafkaHealthIndicator;
import com.orderservice.infrastructure.kafka.OrderKafkaProducer;
import com.orderservice.infrastructure.kafka.OrderKafkaService;
import com.orderservice.infrastructure.kafka.event.ProductOrderEvent;
import com.orderservice.infrastructure.kafka.event.SubmitOrderEvent;
import com.orderservice.infrastructure.kafka.event.SubmitOrderEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderListener {

    private final OrderKafkaProducer orderKafkaProducer;
    private final KafkaHealthIndicator kafkaHealthIndicator;
    private final OrderKafkaService orderKafkaService;

    @Async
    @EventListener
    public void listenSubmitOrderFromProduct(SubmitOrderEvent submitOrderEvent)
        throws JsonProcessingException {
        log.info("submit order from product {}", submitOrderEvent);
        if (kafkaHealthIndicator.isKafkaUp()) {
            orderKafkaProducer.occurSubmitOrderFromProductEvent(submitOrderEvent);
        } else {
            orderKafkaService.occurSubmitOrderFromProductEventFailure(submitOrderEvent);
        }
    }

    @Async
    @EventListener
    public void listenSubmitOrderFromCarts(SubmitOrderEvents submitOrderEvents)
        throws JsonProcessingException {
        log.info("submit order from carts {}", submitOrderEvents);
        if (kafkaHealthIndicator.isKafkaUp()) {
            orderKafkaProducer.occurSubmitOrderFromCartEvent(submitOrderEvents);
        } else {
            orderKafkaService.occurSubmitOrderFromCartEventFailure(submitOrderEvents);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listenSubmitOrderToPayment(ProductOrderEvent productOrderEvent)
        throws JsonProcessingException {
        log.info("submit order to payment {}", productOrderEvent);
        orderKafkaProducer.submitOrderComplete(productOrderEvent);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listenSubmitOrderFromPaymentToDelivery(PaymentEvent paymentEvent)
        throws JsonProcessingException {
        log.info("submit order from payment to delivery {}", paymentEvent);
        orderKafkaProducer.occurDeliveryEvent(paymentEvent.eventResult());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listenSubmitOrderFromDeliveryToProduct(DeliveryEvent deliveryEvent)
        throws JsonProcessingException {
        log.info("submit order from delivery to product {}", deliveryEvent);
        orderKafkaProducer.occurProductEvent(deliveryEvent.eventResult());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listenRollbackPaymentEvent(RollbackPaymentEvent rollbackPaymentEvent)
        throws JsonProcessingException {
        log.info("rollback payment event occurred: {}", rollbackPaymentEvent.eventResult());
        orderKafkaProducer.occurRollbackPaymentEvent(rollbackPaymentEvent.eventResult());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listenRollbackDeliveryEvent(RollbackDeliveryEvent rollbackDeliveryEvent)
        throws JsonProcessingException {
        log.info("rollback delivery event occurred: {}", rollbackDeliveryEvent.eventResult());
        orderKafkaProducer.occurRollbackDeliveryEvent(rollbackDeliveryEvent.eventResult());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listenOrderOutBoxEvent(OrderOutBoxEvent outBoxEvent) {
        log.info("order out box event occurred: {}", outBoxEvent);
        orderKafkaProducer.occurOutBoxEvent(outBoxEvent.topic(), outBoxEvent.message());
    }


}
