package com.ecommerce.config.jwt;

public interface JwtProperties {
    long ACCESS_TOKEN_EXPIRATION_TIME = 6 * 60 * 60 * 1000L; // 6시간
    long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일
    long REFRESH_TOKEN_EXPIRE_TIME_FOR_REDIS = REFRESH_TOKEN_EXPIRE_TIME / 1000; // 7일
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
