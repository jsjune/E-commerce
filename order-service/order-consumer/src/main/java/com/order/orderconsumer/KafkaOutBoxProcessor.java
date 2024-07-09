package com.order.orderconsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.entity.OrderLine;
import com.orderservice.entity.OrderOutBox;
import com.orderservice.entity.ProductOrder;
import com.orderservice.entity.ProductOrderStatus;
import com.orderservice.repository.OrderLineRepository;
import com.orderservice.repository.OrderOutBoxRepository;
import com.orderservice.repository.ProductOrderRepository;
import com.orderservice.usecase.impl.OrderRollbackService;
import com.orderservice.usecase.kafka.KafkaHealthIndicator;
import com.orderservice.usecase.kafka.OrderKafkaProducer;
import com.orderservice.usecase.kafka.OrderKafkaService;
import com.orderservice.usecase.kafka.event.EventResult;
import com.orderservice.usecase.kafka.event.ProductOrderEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
