package com.product.productapi.controller;


import com.product.productcore.config.common.Response;
import com.product.productapi.controller.req.ProductRequestDto;
import com.product.productcore.application.service.ProductReadUseCase;
import com.product.productcore.application.service.ProductWriteUseCase;
import com.product.productcore.application.service.dto.ProductListResponseDto;
import com.product.productcore.application.service.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        productWriteUseCase.createProduct(memberId, request.mapToCommand());
        return Response.success(HttpStatus.OK.value(), null);
    }

    @GetMapping("/{productId}")
    public Response<ProductResponseDto> getProduct(@PathVariable Long productId) throws Exception {
        return Response.success(HttpStatus.OK.value(), productReadUseCase.getProduct(productId));
    }

    @GetMapping
    public Response<ProductListResponseDto> getProducts(@RequestParam String type, @RequestParam String keyword, Pageable pageable) {
        return Response.success(HttpStatus.OK.value(), productReadUseCase.getProducts(type, keyword, pageable));
    }
}
