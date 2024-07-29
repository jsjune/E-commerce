package com.delivery.deliverycore.config.common;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Response<T> {
    private boolean success;
    private int status;
    private T data;
    private LocalDateTime timeStamp;

    public Response(boolean success, int status, T data) {
        this.success = success;
        this.status = status;
        this.data = data;
        this.timeStamp = LocalDateTime.now();
    }

    public static <T> Response<T> success(int status, T data) {
        return new Response<>(true, status, data);
    }

    public static <T> Response<T> fail(int status, T data) {
        return new Response<>(false, status, data);
    }
}
