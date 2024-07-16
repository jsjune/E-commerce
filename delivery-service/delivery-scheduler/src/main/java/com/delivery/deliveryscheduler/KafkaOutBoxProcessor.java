package com.delivery.deliveryscheduler;

import com.delivery.deliverycore.application.service.DeliveryProcessUseCase;
import com.delivery.deliverycore.infrastructure.entity.DeliveryOutBox;
import com.delivery.deliverycore.infrastructure.kafka.DeliveryKafkaProducer;
import com.delivery.deliverycore.infrastructure.kafka.KafkaHealthIndicator;
import com.delivery.deliverycore.infrastructure.kafka.event.EventResult;
import com.delivery.deliverycore.infrastructure.repository.DeliveryOutBoxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final DeliveryOutBoxRepository outBoxRepository;
    private final KafkaHealthIndicator kafkaHealthIndicator;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DeliveryProcessUseCase deliveryProcessUseCase;
    private final DeliveryKafkaProducer deliveryKafkaProducer;
    private final ReentrantLock lock = new ReentrantLock();

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void retry() throws Exception {
        if (lock.tryLock()) {
            try {
                log.info("kafka health check and retrying...");
                List<DeliveryOutBox> findDeliveryBoxes = outBoxRepository.findAllBySuccessFalseNoOffset(
                    LocalDateTime.now(), 100);
                for (DeliveryOutBox outBox : findDeliveryBoxes) {
                    if (kafkaHealthIndicator.isKafkaUp()) {
                        EventResult orderEvent = objectMapper.readValue(outBox.getMessage(),
                            EventResult.class);
                        Long deliveryId = deliveryProcessUseCase.processDelivery(
                            orderEvent.mapToCommand());
                        int status = deliveryId == -1L ? -1 : 1;
                        orderEvent = orderEvent.assignDeliveryIdAndStatus(deliveryId, status);
                        deliveryKafkaProducer.occurDeliveryEvent(orderEvent);
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
