package com.payment.paymentcore.infrastructure.repository;


import static com.payment.paymentcore.infrastructure.entity.QPaymentOutBox.paymentOutBox;

import com.payment.paymentcore.infrastructure.entity.PaymentOutBox;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomPaymentOutBoxRepositoryImpl implements CustomPaymentOutBoxRepository {

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<PaymentOutBox> findAllBySuccessFalseNoOffset(LocalDateTime now, int limit) {
        return jpaQueryFactory.selectFrom(paymentOutBox)
            .where(paymentOutBox.success.eq(false)
                .and(paymentOutBox.createdAt.lt(now)))
            .orderBy(paymentOutBox.createdAt.asc())
            .limit(limit)
            .fetch();
    }
}
