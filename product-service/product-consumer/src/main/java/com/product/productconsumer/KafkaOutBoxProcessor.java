package com.product.productconsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.entity.ProductOutBox;
import com.productservice.repository.ProductOutBoxRepository;
import com.productservice.usecase.ProductWriteUseCase;
import com.productservice.usecase.kafka.KafkaHealthIndicator;
import com.productservice.usecase.kafka.ProductKafkaProducer;
import com.productservice.usecase.kafka.event.EventResult;
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
    private final ProductWriteUseCase productWriteUseCase;
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
                        int status = productWriteUseCase.decreaseStock(
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
