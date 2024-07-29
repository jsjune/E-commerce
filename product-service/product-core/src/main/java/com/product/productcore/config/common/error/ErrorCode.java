package com.product.productcore.config.common.error;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST, "PRODUCT_NOT_FOUND"),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "INVALID_FILE_EXTENSION"),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_FILE_FORMAT"),
    UNSUPPORTED_MIME_TYPE(HttpStatus.BAD_REQUEST, "UNSUPPORTED_MIME_TYPE"),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER_NOT_FOUND");

    private HttpStatus httpStatus;
    private String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
