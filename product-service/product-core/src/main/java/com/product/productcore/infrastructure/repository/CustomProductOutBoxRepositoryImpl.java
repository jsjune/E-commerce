package com.product.productcore.infrastructure.repository;

import static com.product.productcore.infrastructure.entity.QProductOutBox.productOutBox;

import com.product.productcore.infrastructure.entity.ProductOutBox;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomProductOutBoxRepositoryImpl implements CustomProductOutBoxRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProductOutBox> findAllBySuccessFalseNoOffset(LocalDateTime now, int limit) {
        return jpaQueryFactory.selectFrom(productOutBox)
            .where(productOutBox.success.eq(false)
                .and(productOutBox.createdAt.lt(now)))
            .orderBy(productOutBox.createdAt.asc())
            .limit(limit)
            .fetch();
    }
}
