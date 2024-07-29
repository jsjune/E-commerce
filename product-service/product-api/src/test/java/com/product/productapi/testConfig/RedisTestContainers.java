package com.product.productapi.testConfig;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@DisplayName("Redis Test Containers")
@ActiveProfiles("test")
@Configuration
public class RedisTestContainers {

    private static final String REDIS_DOCKER_IMAGE = "redis:5.0.3-alpine";
    private static final GenericContainer<?> REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse(REDIS_DOCKER_IMAGE))
            .withExposedPorts(6379)
            .withReuse(true);
        REDIS_CONTAINER.start();

        System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        String host = REDIS_CONTAINER.getHost();
        int port = REDIS_CONTAINER.getMappedPort(6379);
        return new LettuceConnectionFactory(host, port);
    }


}
