package com.productservice.controller.internal;

import com.productservice.controller.internal.res.ProductDto;
import com.productservice.usecase.ProductReadUseCase;
import com.productservice.usecase.ProductWriteUseCase;
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
    private final ProductReadUseCase productReadUseCase;
    private final ProductWriteUseCase productWriteUseCase;

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable Long productId) {
        return productReadUseCase.findProductById(productId);
    }

//    @PostMapping("/{productId}/decrease")
//    public Boolean decreaseStock(@PathVariable Long productId, @RequestParam int quantity) {
//        return productWriteUseCase.decreaseStock(productId, quantity);
//    }

    @PostMapping("/{productId}/increment")
    public Boolean incrementStock(@PathVariable Long productId, @RequestParam Long quantity) {
        return productWriteUseCase.incrementStock(productId, quantity);
    }
}
