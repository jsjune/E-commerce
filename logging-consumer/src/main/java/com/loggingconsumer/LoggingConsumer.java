package com.loggingconsumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingConsumer {

    @KafkaListener(topics = "logging-topic", groupId = "logging-group")
    public void listen(ConsumerRecord<String, String> record) {
        if (record.value().contains("Exception")) {
            log.error("Received message: {}", record.value());
        } else {
            log.info("Received message: {}", record.value());
        }
    }
}
