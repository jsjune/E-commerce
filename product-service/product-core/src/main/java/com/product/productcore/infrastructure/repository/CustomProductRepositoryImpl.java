package com.product.productcore.infrastructure.repository;

import static com.product.productcore.infrastructure.entity.QProduct.product;

import com.product.productcore.infrastructure.entity.Product;
import com.product.productcore.infrastructure.entity.QProduct;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Product> searchAll(String type, String keyword, Pageable pageable) {
        List<Long> productIds = new ArrayList<>();
        if (type.equals("name")) {
            productIds = jpaQueryFactory.select(product.id)
                .from(product)
                .where(product.name.like(keyword.concat("%")))
                .orderBy(product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        } else if (type.equals("tag")) {
            productIds = jpaQueryFactory.select(product.id)
                .from(product)
                .where(product.tags.any().eq(keyword))
                .orderBy(product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        }

        return jpaQueryFactory.selectFrom(product)
            .where(product.id.in(productIds))
            .orderBy(getOrderSpecifiers(pageable.getSort(), product))
            .fetch();
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort, QProduct product) {
        return sort.stream()
            .map(order -> {
                if ("id".equals(order.getProperty())) {
                    return order.isAscending() ? product.id.asc() : product.id.desc();
                } else if ("soldQuantity".equals(order.getProperty())) {
                    return order.isAscending() ? product.soldQuantity.asc() : product.soldQuantity.desc();
                } else {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toArray(OrderSpecifier[]::new);
    }

}
