package com.ecommerce.common.error;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND"),
    EXIST_MEMBER(HttpStatus.BAD_REQUEST, "EXIST_MEMBER"),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_JWT_TOKEN"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Token Expired"),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED,"UNSUPPORTED_JWT_TOKEN"),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS");

    private HttpStatus httpStatus;
    private String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
