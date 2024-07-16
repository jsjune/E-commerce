package com.product.productapi.controller.internal;

import com.product.productapi.usecase.InternalProductUseCase;
import com.product.productapi.usecase.ProductReadUseCase;
import com.product.productapi.usecase.ProductWriteUseCase;
import com.product.productapi.usecase.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/products")
public class ProductInternalController {
    private final InternalProductUseCase internalProductUseCase;

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable Long productId) {
        return internalProductUseCase.findProductById(productId);
    }

    @PostMapping("/{productId}/decrease")
    public int decreaseStock(@PathVariable Long productId, @RequestParam Long quantity) {
        return internalProductUseCase.decreaseStock(productId, quantity);
    }

    @PostMapping("/{productId}/increment")
    public Boolean incrementStock(@PathVariable Long productId, @RequestParam Long quantity) {
        return internalProductUseCase.incrementStock(productId, quantity);
    }
}
