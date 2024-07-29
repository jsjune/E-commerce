package com.delivery.deliverycore.config.common.error;

import com.delivery.deliverycore.config.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(value = GlobalException.class)
    public ResponseEntity<?> handleGlobalException(GlobalException e) {
        log.warn("error message {}", e.getErrorCode().getMessage(), e);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(Response.fail(e.getErrorCode().getHttpStatus().value(), e.getErrorCode().getMessage()));
    }

}
