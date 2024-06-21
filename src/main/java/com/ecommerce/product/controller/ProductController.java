package com.ecommerce.product.controller;

import com.ecommerce.common.Response;
import com.ecommerce.member.auth.LoginUser;
import com.ecommerce.product.controller.req.ProductRequestDto;
import com.ecommerce.product.controller.res.ProductListResponseDto;
import com.ecommerce.product.controller.res.ProductResponseDto;
import com.ecommerce.product.usecase.ProductReadUseCase;
import com.ecommerce.product.usecase.ProductWriteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductWriteUseCase productWriteUseCase;
    private final ProductReadUseCase productReadUseCase;

    @PostMapping("/products")
    public Response<Void> createProduct(@AuthenticationPrincipal LoginUser loginUser, @ModelAttribute ProductRequestDto request) {
        productWriteUseCase.createProduct(loginUser, request);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @GetMapping("/products/{id}")
    public Response<ProductResponseDto> getProduct(@PathVariable Long id) {
        return Response.success(HttpStatus.OK.value(), productReadUseCase.getProduct(id));
    }

    @GetMapping("/products")
    public Response<ProductListResponseDto> getProducts(Pageable pageable) {
        return Response.success(HttpStatus.OK.value(), productReadUseCase.getProducts(pageable));
    }
}
