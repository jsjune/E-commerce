package com.deliveryservice.utils.error;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    PRODUCT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "PRODUCT_NOT_FOUND"),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST,"PAYMENT_FAILED"),
    DELIVERY_FAILED(HttpStatus.BAD_REQUEST, "DELIVERY_FAILED"),
    PAYMENT_METHOD_NOT_FOUND(HttpStatus.BAD_REQUEST, "PAYMENT_METHOD_NOT_FOUND"),
    DELIVERY_ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST, "DELIVERY_ADDRESS_NOT_FOUND"),
    PRODUCT_STOCK_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "PRODUCT_STOCK_NOT_ENOUGH"),
    DELIVERY_STATUS_NOT_REQUESTED(HttpStatus.BAD_REQUEST, "DELIVERY_STATUS_NOT_REQUESTED");

    private HttpStatus httpStatus;
    private String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
