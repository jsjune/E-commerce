package com.product.productconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.productcore.infrastructure.kafka.KafkaHealthIndicator;
import com.product.productcore.infrastructure.kafka.ProductKafkaService;
import com.product.productcore.infrastructure.kafka.event.EventResult;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductKafkaService productKafkaService;
    private final KafkaHealthIndicator kafkaHealthIndicator;
    private final RedissonClient redissonClient;

    @KafkaListener(topics = "${consumers.topic1}", groupId = "${consumers.groupId}")
    public void consumeProduct(ConsumerRecord<String, String> record) {
        try {
            EventResult orderEvent = objectMapper.readValue(record.value(), EventResult.class);
            RLock lock = redissonClient.getLock(
                "product.lock=" + orderEvent.orderLine().productId());
            boolean lockAcquired = false;
            try {
                lockAcquired = lock.tryLock(5, 10, TimeUnit.SECONDS);
                if (!lockAcquired) {
                    log.error("Failed to acquire lock");
                    throw new RuntimeException("Failed to acquire lock");
                }
                orderEvent = productKafkaService.decreaseStock(orderEvent);
            } catch (InterruptedException e) {
                log.error("Failed to acquire lock");
                throw new InterruptedException("Failed to acquire lock");
            }finally {
                if (lockAcquired) { // 락을 획득한 경우에만 해제
                    lock.unlock();
                    if (kafkaHealthIndicator.isKafkaUp()) {
                        productKafkaService.handleProduct(orderEvent);
                    } else {
                        log.error("Failed to send order event");
                        productKafkaService.occurProductFailure(orderEvent);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to consume product event", e);
            throw new RuntimeException("Failed to consume product event");
        }
    }
}
