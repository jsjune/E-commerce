package com.member.membercore.config.common.error;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND"),
    PRODUCT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "PRODUCT_NOT_FOUND"),
    EXIST_MEMBER(HttpStatus.BAD_REQUEST, "EXIST_MEMBER"),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS"),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_EMAIL_FORMAT");

    private HttpStatus httpStatus;
    private String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
