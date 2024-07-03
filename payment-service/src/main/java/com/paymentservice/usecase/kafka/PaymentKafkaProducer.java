package com.paymentservice.usecase.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentservice.entity.PaymentOutBox;
import com.paymentservice.repository.PaymentOutBoxRepository;
import com.paymentservice.usecase.PaymentUseCase;
import com.paymentservice.usecase.kafka.event.EventResult;
import com.paymentservice.usecase.kafka.event.ProductOrderEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentKafkaProducer {

    @Value(value = "${producers.topic1}")
    public String PAYMENT_TOPIC;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PaymentOutBoxRepository outBoxRepository;
    private final KafkaHealthIndicator kafkaHealthIndicator;
    private final PaymentUseCase paymentUseCase;

    public void occurPaymentEvent(EventResult eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(PAYMENT_TOPIC,
            json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[payment_request] sent: " + eventResult);
            } else {
                log.error("[payment_request] failed to send: " + eventResult + ex);
            }
        });
    }

    @Transactional
    public void occurPaymentFailure(ProductOrderEvent eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        PaymentOutBox outBox = PaymentOutBox.builder()
            .topic(PAYMENT_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
    }


    @Transactional
    @Scheduled(fixedRate = 30000)
    public void retry() throws Exception {
        log.info("kafka health check and retrying...");
        List<PaymentOutBox> findPaymentOutBoxes = outBoxRepository.findAllBySuccessFalseNoOffset(
            LocalDateTime.now(), 5);
        for (PaymentOutBox outBox : findPaymentOutBoxes) {
            if (kafkaHealthIndicator.isKafkaUp()) {
                ProductOrderEvent orderEvent = objectMapper.readValue(
                    outBox.getMessage(), ProductOrderEvent.class);
                Long paymentId = paymentUseCase.processPayment(orderEvent.mapToCommand());
                int status = paymentId == -1L ? -1 : 1;
                EventResult eventResult = orderEvent.mapToEventResult(paymentId, status);
                this.occurPaymentEvent(eventResult);
                outBoxRepository.delete(outBox);
            }
        }
    }

}

