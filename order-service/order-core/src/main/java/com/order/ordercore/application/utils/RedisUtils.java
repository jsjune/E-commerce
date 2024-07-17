package com.order.ordercore.application.utils;

import com.ecommerce.common.cache.CachingCartListDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, List<CachingCartListDto>> objectRedisTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final String PRODUCT_KEY = "product.stock=";

    public List<CachingCartListDto> getCartList(Long memberId) {
        return objectRedisTemplate.opsForValue()
            .get(String.format("cartList::%s", memberId));
    }

    public Boolean decreaseStock(Long productId, Long quantity) {
        String script = """
                local currentStock = redis.call('GET', KEYS[1])
                if not currentStock then
                    return false
                end
                if tonumber(currentStock) < tonumber(ARGV[1]) then
                    return false
                end
                redis.call('DECRBY', KEYS[1], tonumber(ARGV[1]))
                return true
                """;
        RedisScript<Boolean> stringRedisScript = RedisScript.of(script, Boolean.class);
        return redisTemplate.execute(stringRedisScript,
            List.of(PRODUCT_KEY + productId), quantity.toString());
    }

    public void increaseStock(Long productId, Long quantity) {
        redisTemplate.opsForValue().increment(PRODUCT_KEY + productId, quantity);
    }
}
