package com.ecommerce.payment.adapter.impl;

import com.ecommerce.payment.adapter.PaymentAdapter;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PaymentAdapterImpl implements PaymentAdapter {

    @Override
    public String processPayment(int totalPrice, String creditCardNumber, String accountNumber,
        String cardNumber) {
        // actual process with external system
        return UUID.randomUUID().toString();
    }
}
