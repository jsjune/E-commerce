package com.product.productcore.application.service;

public interface ProductDecreaseUseCase {
    int decreaseStock(Long productId, Long quantity);

}
