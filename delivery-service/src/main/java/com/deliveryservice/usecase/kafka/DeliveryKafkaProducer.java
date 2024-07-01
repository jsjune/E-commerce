package com.deliveryservice.usecase.kafka;

import com.deliveryservice.entity.DeliveryOutBox;
import com.deliveryservice.repository.DeliveryOutBoxRepository;
import com.deliveryservice.usecase.DeliveryUseCase;
import com.deliveryservice.usecase.kafka.event.EventResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryKafkaProducer {

    @Value(value = "${producers.topic1}")
    private String DELIVERY_TOPIC;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DeliveryOutBoxRepository outBoxRepository;
    private final KafkaHealthIndicator kafkaHealthIndicator;
    private final DeliveryUseCase deliveryUseCase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void occurDeliveryEvent(EventResult eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(DELIVERY_TOPIC,
            json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[delivery_request] sent: " + eventResult);
            } else {
                log.error("[delivery_request] failed to send: " + eventResult + ex);
            }
        });
    }

    @Transactional
    public void occurDeliveryFailure(EventResult orderEvent) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(orderEvent);
        DeliveryOutBox outBox = DeliveryOutBox.builder()
            .topic(DELIVERY_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
    }

    @Transactional
    @Scheduled(fixedRate = 10000)
    public void retry() throws Exception {
        log.info("kafka health check and retrying...");
        List<DeliveryOutBox> findDeliveryBoxes = outBoxRepository.findAllBySuccessFalse();
        for (DeliveryOutBox outBox : findDeliveryBoxes) {
            if (kafkaHealthIndicator.isKafkaUp()) {
                EventResult orderEvent = objectMapper.readValue(outBox.getMessage(),
                    EventResult.class);
                Long deliveryId = deliveryUseCase.processDelivery(orderEvent.mapToCommand());
                int status = deliveryId == -1L ? -1 : 1;
                orderEvent = orderEvent.assignDeliveryIdAndStatus(deliveryId, status);
                this.occurDeliveryEvent(orderEvent);
                outBoxRepository.delete(outBox);
            }
        }
    }
}
