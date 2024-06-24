package com.productservice.usecase.impl;

import com.productservice.adapter.MemberClient;
import com.productservice.adapter.dto.MemberDto;
import com.productservice.utils.AesUtil;
import com.productservice.utils.error.ErrorCode;
import com.productservice.utils.error.GlobalException;
import com.productservice.controller.req.ProductRequestDto;
import com.productservice.entity.Product;
import com.productservice.entity.ProductImage;
import com.productservice.repository.ProductRepository;
import com.productservice.usecase.ProductWriteUseCase;
import com.productservice.utils.ImageValidator;
import com.productservice.utils.S3Utils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductWriteService implements ProductWriteUseCase {

    private final MemberClient memberClient;
    private final ProductRepository productRepository;
    private final S3Utils s3Utils;
    private final AesUtil aesUtil;
    private static final String UPLOAD_FOLDER = "images";

    @Override
    public void createProduct(Long memberId, ProductRequestDto request) throws Exception {
        MemberDto member = memberClient.getMemberInfo(memberId);
        List<ProductImage> images = new ArrayList<>();
        for (MultipartFile image : request.getProductImages()) {
            if (!image.isEmpty()) {
                ImageValidator.validateImageFile(image);
                String datePath = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy/MM-dd"));
                String uploadFolder = UPLOAD_FOLDER + "/" + datePath + "/" + member.company();
                String uploadImageName = UUID.randomUUID() + "_" + image.getOriginalFilename();

                String s3Path = uploadFolder + "/" + uploadImageName;
                String orgImageUrl = s3Utils.uploadFile(image, s3Path);
                String thumbS3Path = uploadFolder + "/s_" + uploadImageName;
                String thumbnailUrl = s3Utils.uploadThumbFile(image, thumbS3Path);
                images.add(new ProductImage(orgImageUrl, s3Path, thumbnailUrl, thumbS3Path));
            }
        }

        Product product = Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .totalStock(request.getStock())
            .soldQuantity(0)
            .sellerId(member.memberId())
            .phoneNumber(aesUtil.aesEncode(member.phoneNumber()))
            .company(member.company())
            .tags(request.getTags())
            .productImages(images)
            .build();
        productRepository.save(product);
    }

    @Override
    public void decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new GlobalException(ErrorCode.PRODUCT_NOT_FOUND));
        if(product.getTotalStock() < quantity) {
            throw new GlobalException(ErrorCode.PRODUCT_STOCK_NOT_ENOUGH);
        }
        product.decreaseStock(quantity);
    }

    @Override
    public void incrementStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new GlobalException(ErrorCode.PRODUCT_NOT_FOUND));
        product.incrementStock(quantity);
    }
}
