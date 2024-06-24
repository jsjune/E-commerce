package com.productservice.utils.error;


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
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS"),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_EMAIL_FORMAT"),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST,"PAYMENT_FAILED"),
    DELIVERY_FAILED(HttpStatus.BAD_REQUEST, "DELIVERY_FAILED"),
    PAYMENT_METHOD_NOT_FOUND(HttpStatus.BAD_REQUEST, "PAYMENT_METHOD_NOT_FOUND"),
    DELIVERY_ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST, "DELIVERY_ADDRESS_NOT_FOUND"),
    PRODUCT_STOCK_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "PRODUCT_STOCK_NOT_ENOUGH"),
    DELIVERY_STATUS_NOT_REQUESTED(HttpStatus.BAD_REQUEST, "DELIVERY_STATUS_NOT_REQUESTED"),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER_NOT_FOUND");

    private HttpStatus httpStatus;
    private String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
