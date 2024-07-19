package com.product.productcore.application.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, String> redisTemplate;

    public Long getStock(String key) {
        String stock = redisTemplate.opsForValue().get(key);
        return stock == null ? 0 : Long.parseLong(stock);
    }
}
