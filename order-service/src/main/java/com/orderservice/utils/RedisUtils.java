package com.orderservice.utils;

import com.ecommerce.common.cache.CartListDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, List<CartListDto>> redisTemplate;

    public List<CartListDto> getCartList(Long memberId) {
        return redisTemplate.opsForValue()
            .get(String.format("cartList::%s", memberId));
    }
}
