package com.ecommerce.payment.usecase;

import com.ecommerce.member.entity.Member;
import com.ecommerce.order.entity.OrderLine;
import com.ecommerce.order.entity.ProductOrder;
import com.ecommerce.payment.entity.Payment;
import java.util.Optional;

public interface PaymentUseCase {

    Payment processPayment(Member member, OrderLine orderLine, Long paymentMethodId)
        throws Exception;
}
