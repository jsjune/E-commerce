package com.orderservice.usecase.dto;

import com.orderservice.infrastructure.kafka.event.EventResult;

public record RollbackDeliveryEvent(EventResult eventResult) {

}
