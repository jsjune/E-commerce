package com.order.ordercore.application.service.dto;


import com.order.ordercore.infrastructure.kafka.event.EventResult;

public record PaymentEvent(EventResult eventResult) { }
