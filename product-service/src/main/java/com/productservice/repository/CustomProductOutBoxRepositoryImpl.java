package com.productservice.repository;

import static com.productservice.entity.QProductOutBox.productOutBox;

import com.productservice.entity.ProductOutBox;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomProductOutBoxRepositoryImpl implements CustomProductOutBoxRepository{
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
