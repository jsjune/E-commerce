package com.paymentservice.usecase.dto;

public record PaymentDto(Long paymentId, int totalPrice, int status) {

}
