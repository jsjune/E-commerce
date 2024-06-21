package com.ecommerce.common.error;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND"),
    PRODUCT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "PRODUCT_NOT_FOUND"),
    EXIST_MEMBER(HttpStatus.BAD_REQUEST, "EXIST_MEMBER"),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "INVALID_FILE_EXTENSION"),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_FILE_FORMAT"),
    UNSUPPORTED_MIME_TYPE(HttpStatus.BAD_REQUEST, "UNSUPPORTED_MIME_TYPE"),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_JWT_TOKEN"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Token Expired"),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED,"UNSUPPORTED_JWT_TOKEN"),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS"),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_EMAIL_FORMAT");

    private HttpStatus httpStatus;
    private String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
