package com.productservice.repository;

import static com.productservice.entity.QProduct.product;

import com.productservice.entity.Product;
import com.productservice.usecase.ProductReadUseCase;
import com.productservice.usecase.dto.ProductListResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
class CustomProductRepositoryImplTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductReadUseCase productReadUseCase;
    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

    }

    @Test
    void index() {
        Pageable pageable = PageRequest.of(0, 20);
        String keyword = "a";
        Instant start = Instant.now();
        List<Product> products = jpaQueryFactory.selectFrom(product)
            .where(product.name.like(keyword.concat("%")))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(product.id.desc(), product.soldQuantity.desc())
            .fetch();
        Long count = jpaQueryFactory.select(product.count())
            .from(product)
            .where(product.name.like(keyword.concat("%")))
            .fetchOne();
        Instant end = Instant.now();
        System.out.println("검색 소요 시간: " + (end.toEpochMilli() - start.toEpochMilli()) + "ms");
    }

    @Test
    void covering_index() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        String keyword = "a";
        String type = "name";

        Instant start = Instant.now();
        List<Product> products = productRepository.searchAll(type, keyword, pageable);
        Instant end = Instant.now();
        System.out.println("검색 소요 시간: " + (end.toEpochMilli() - start.toEpochMilli()) + "ms");
    }

    @Test
    void cashing() {
        Pageable pageable = PageRequest.of(0, 20);
        String keyword = "a";
        String type = "name";

        Instant start = Instant.now();
        ProductListResponseDto products = productReadUseCase.getProducts(type, keyword, pageable);
        Instant end = Instant.now();
        System.out.println("검색 소요 시간: " + (end.toEpochMilli() - start.toEpochMilli()) + "ms");

    }

    //    @Test
    void bulkInsert() {
        int batchSize = 10000; // 한 번에 처리할 배치 크기
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < 2_000_000; i++) {
            products.add(Product.builder().name(UUID.randomUUID().toString()).build());

            if (products.size() == batchSize) {
                batchInsert(products);
                products.clear();
                System.out.println("완료 : " + i);
            }
        }
    }

    private void batchInsert(List<Product> posts) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO product (name) VALUES (?)",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, posts.get(i).getName());
                }

                @Override
                public int getBatchSize() {
                    return posts.size();
                }
            }
        );
    }
}
