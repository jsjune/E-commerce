package com.orderservice.payment.adapter;

public interface PaymentAdapter {
    String processPayment(int totalPrice, String creditCardNumber, String accountNumber,
        String cardNumber);
}
