package com.ecommerce.common;

import lombok.Getter;

@Getter
public class Response<T> {
    private boolean success;
    private int status;
    private T data;

    public Response(boolean success, int status, T data) {
        this.success = success;
        this.status = status;
        this.data = data;
    }

    public static <T> Response<T> success(int status, T data) {
        return new Response<>(true, status, data);
    }

    public static <T> Response<T> fail(int status, T data) {
        return new Response<>(false, status, data);
    }
}
