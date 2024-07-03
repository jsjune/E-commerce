package com.deliveryservice.repository;

import static com.deliveryservice.entity.QDeliveryOutBox.deliveryOutBox;

import com.deliveryservice.entity.DeliveryOutBox;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomDeliveryOutBoxRepositoryImpl implements CustomDeliveryOutBoxRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<DeliveryOutBox> findAllBySuccessFalseNoOffset(LocalDateTime now, int limit) {
        return jpaQueryFactory.selectFrom(deliveryOutBox)
            .where(deliveryOutBox.success.eq(false)
                .and(deliveryOutBox.createdAt.lt(now)))
            .orderBy(deliveryOutBox.createdAt.asc())
            .limit(limit)
            .fetch();
    }
}
