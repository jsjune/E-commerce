package com.payment.paymentcore.adapter;

public interface PaymentAdapter {
    String processPayment(Long totalPrice, String creditCardNumber, String accountNumber,
        String cardNumber);

    String cancelPayment(Long totalPrice, String bank, String accountNumber, String creditCardNumber);
}
