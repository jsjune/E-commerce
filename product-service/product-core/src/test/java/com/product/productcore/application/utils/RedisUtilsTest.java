package com.product.productcore.application.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.product.productcore.testConfig.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

class RedisUtilsTest extends IntegrationTestSupport {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DisplayName("레디스에서 재고를 가져온다.")
    @Test
    void getStock() {
        // given
        Long productId = 1L;
        Long stock = 10L;
        redisTemplate.opsForValue().set(String.format("product.stock.test=%s", productId), String.format("%s", stock));

        // when
        Long result = redisUtils.getStock(String.format("product.stock.test=%s", productId));

        // then
        assertEquals(stock, result);
        redisTemplate.delete(String.format("product.stock.test=%s", productId));
    }
}
