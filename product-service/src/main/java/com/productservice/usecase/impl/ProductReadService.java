package com.productservice.usecase.impl;

import com.productservice.adapter.dto.ProductDto;
import com.productservice.utils.error.ErrorCode;
import com.productservice.utils.error.GlobalException;
import com.productservice.controller.res.ProductListDto;
import com.productservice.controller.res.ProductListResponseDto;
import com.productservice.controller.res.ProductResponseDto;
import com.productservice.entity.Product;
import com.productservice.entity.ProductImage;
import com.productservice.repository.ProductRepository;
import com.productservice.usecase.ProductReadUseCase;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductReadService implements ProductReadUseCase {
    private final ProductRepository productRepository;

    @Override
    public ProductResponseDto getProduct(Long productId) {
        return productRepository.findById(productId)
            .map(product -> ProductResponseDto.builder()
                .sellerId(product.getSellerId())
                .company(product.getCompany())
                .phoneNumber(product.getPhoneNumber())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .tags(product.getTags())
                .orgProductImages(product.getProductImages().stream()
                    .map(ProductImage::getOrgImageUrl)
                    .collect(Collectors.toList()))
                .build())
            .orElseThrow(() -> new GlobalException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public ProductListResponseDto getProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductListDto> response = products.getContent().stream()
            .map(this::mapToProductResponse)
            .collect(Collectors.toList());
        return new ProductListResponseDto(response, products.getNumber(), products.getTotalPages());
    }

    @Override
    public ProductDto findProductById(Long productId) {
        return productRepository.findById(productId).map(ProductDto::new)
            .orElseThrow(() -> new GlobalException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private ProductListDto mapToProductResponse(Product product) {
        return ProductListDto.builder()
            .productId(product.getId())
            .sellerId(product.getSellerId())
            .company(product.getCompany())
            .phoneNumber(product.getPhoneNumber())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .tags(product.getTags())
            .orgProductImages(product.getProductImages().stream()
                .map(ProductImage::getThumbnailUrl)
                .collect(Collectors.toList()))
            .build();
    }


}
