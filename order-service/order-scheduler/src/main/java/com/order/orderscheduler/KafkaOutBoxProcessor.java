package com.order.orderscheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order.ordercore.infrastructure.entity.OrderOutBox;
import com.order.ordercore.infrastructure.kafka.KafkaHealthIndicator;
import com.order.ordercore.infrastructure.kafka.OrderKafkaService;
import com.order.ordercore.infrastructure.repository.OrderOutBoxRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaOutBoxProcessor {

    private final OrderOutBoxRepository outBoxRepository;
    private final KafkaHealthIndicator kafkaHealthIndicator;
    private final OrderKafkaService orderKafkaService;
    private final ReentrantLock lock = new ReentrantLock();

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void retry() throws JsonProcessingException {
        if (lock.tryLock()) {
            try {
                log.info("kafka health check and retrying...");
                List<OrderOutBox> findOrderOutBoxes = outBoxRepository.findAllBySuccessFalseNoOffset(
                    LocalDateTime.now(), 100);
                for (OrderOutBox outBox : findOrderOutBoxes) {
                    if (kafkaHealthIndicator.isKafkaUp()) {
                        orderKafkaService.processOutboxMessage(outBox);
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
