package com.paymentservice.adapter;

public interface PaymentAdapter {
    String processPayment(int totalPrice, String creditCardNumber, String accountNumber,
        String cardNumber);
}
