package com.payment.paymentconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentservice.entity.PaymentOutBox;
import com.paymentservice.repository.PaymentOutBoxRepository;
import com.paymentservice.usecase.PaymentUseCase;
import com.paymentservice.usecase.kafka.KafkaHealthIndicator;
import com.paymentservice.usecase.kafka.PaymentKafkaProducer;
import com.paymentservice.usecase.kafka.event.EventResult;
import com.paymentservice.usecase.kafka.event.ProductOrderEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaOutBoxProcessor {

    private final PaymentOutBoxRepository outBoxRepository;
    private final KafkaHealthIndicator kafkaHealthIndicator;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PaymentUseCase paymentUseCase;
    private final PaymentKafkaProducer paymentKafkaProducer;
    private final ReentrantLock lock = new ReentrantLock();

    @Transactional
    @Scheduled(fixedRate = 30000)
    public void retry() throws Exception {
        if (lock.tryLock()) {
            try {
                log.info("kafka health check and retrying...");
                List<PaymentOutBox> findPaymentOutBoxes = outBoxRepository.findAllBySuccessFalseNoOffset(
                    LocalDateTime.now(), 100);
                for (PaymentOutBox outBox : findPaymentOutBoxes) {
                    if (kafkaHealthIndicator.isKafkaUp()) {
                        ProductOrderEvent orderEvent = objectMapper.readValue(
                            outBox.getMessage(), ProductOrderEvent.class);
                        Long paymentId = paymentUseCase.processPayment(orderEvent.mapToCommand());
                        int status = paymentId == -1L ? -1 : 1;
                        EventResult eventResult = orderEvent.mapToEventResult(paymentId, status);
                        paymentKafkaProducer.occurPaymentEvent(eventResult);
                        outBoxRepository.delete(outBox);
                    }
                }
            } finally {
                lock.unlock();
            }
        } else {
            log.info("Previous task is still running. Skipping this run.");
        }
    }
}
