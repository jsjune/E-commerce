package com.product.productcore.application.utils;

import java.util.function.Function;

public class ExceptionWrapper {
    @FunctionalInterface
    public interface FunctionWithException<T, R> {
        R apply(T t) throws Exception;
    }

    public static <T, R> Function<T, R> wrap(FunctionWithException<T, R> functionWithException) {
        return i -> {
            try {
                return functionWithException.apply(i);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

}
