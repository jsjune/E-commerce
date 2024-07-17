package com.order.ordercore.infrastructure.repository;


import static com.order.ordercore.infrastructure.entity.QOrderOutBox.orderOutBox;

import com.order.ordercore.infrastructure.entity.OrderOutBox;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomOrderOutBoxRepositoryImpl implements CustomOrderOutBoxRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<OrderOutBox> findAllBySuccessFalseNoOffset(LocalDateTime now, int limit) {
        return jpaQueryFactory.selectFrom(orderOutBox)
            .where(orderOutBox.success.eq(false)
                .and(orderOutBox.createdAt.lt(now)))
            .orderBy(orderOutBox.createdAt.asc())
            .limit(limit)
            .fetch();
    }
}
