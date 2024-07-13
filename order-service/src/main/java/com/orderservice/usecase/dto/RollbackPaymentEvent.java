package com.orderservice.usecase.dto;

import com.orderservice.usecase.kafka.event.EventResult;

public record RollbackPaymentEvent(EventResult eventResult) {

}
