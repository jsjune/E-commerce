package com.product.productscheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.productcore.application.service.ProductDecreaseUseCase;
import com.product.productcore.infrastructure.entity.ProductOutBox;
import com.product.productcore.infrastructure.kafka.KafkaHealthIndicator;
import com.product.productcore.infrastructure.kafka.ProductKafkaProducer;
import com.product.productcore.infrastructure.kafka.event.EventResult;
import com.product.productcore.infrastructure.repository.ProductOutBoxRepository;
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

    private final ProductOutBoxRepository outBoxRepository;
    private final KafkaHealthIndicator kafkaHealthIndicator;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductDecreaseUseCase productDecreaseUseCase;
    private final ProductKafkaProducer productKafkaProducer;
    private final ReentrantLock lock = new ReentrantLock();

    @Transactional
    @Scheduled(fixedRate = 30000)
    public void retry() throws JsonProcessingException {
        if (lock.tryLock()) {
            try {
                log.info("kafka health check and retrying...");
                List<ProductOutBox> findProductOutBoxes = outBoxRepository.findAllBySuccessFalseNoOffset(
                    LocalDateTime.now(), 10);
                for (ProductOutBox outBox : findProductOutBoxes) {
                    if (kafkaHealthIndicator.isKafkaUp()) {
                        EventResult orderEvent = objectMapper.readValue(outBox.getMessage(),
                            EventResult.class);
                        int status = productDecreaseUseCase.decreaseStock(
                            orderEvent.orderLine().productId(),
                            orderEvent.orderLine().quantity());
                        orderEvent = orderEvent.withStatus(status);
                        productKafkaProducer.occurProductEvent(orderEvent);
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
