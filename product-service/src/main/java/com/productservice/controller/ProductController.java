package com.productservice.controller;


import com.productservice.utils.Response;
import com.productservice.controller.req.ProductRequestDto;
import com.productservice.controller.res.ProductListResponseDto;
import com.productservice.controller.res.ProductResponseDto;
import com.productservice.usecase.ProductReadUseCase;
import com.productservice.usecase.ProductWriteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductWriteUseCase productWriteUseCase;
    private final ProductReadUseCase productReadUseCase;

    @PostMapping
    public Response<Void> createProduct(
        @RequestHeader("Member-Id")Long memberId,
        @ModelAttribute ProductRequestDto request) throws Exception {
        productWriteUseCase.createProduct(memberId, request);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @GetMapping("/{productId}")
    public Response<ProductResponseDto> getProduct(@PathVariable Long productId) throws Exception {
        return Response.success(HttpStatus.OK.value(), productReadUseCase.getProduct(productId));
    }

    @GetMapping
    public Response<ProductListResponseDto> getProducts(Pageable pageable) {
        return Response.success(HttpStatus.OK.value(), productReadUseCase.getProducts(pageable));
    }
}
