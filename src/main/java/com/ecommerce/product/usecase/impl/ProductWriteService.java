package com.ecommerce.product.usecase.impl;

import com.ecommerce.product.utils.ImageValidator;
import com.ecommerce.product.utils.S3Utils;
import com.ecommerce.member.auth.LoginUser;
import com.ecommerce.product.controller.req.ProductRequestDto;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductImage;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.usecase.ProductWriteUseCase;
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

    private final ProductRepository productRepository;
    private final S3Utils s3Utils;
    private static final String UPLOAD_FOLDER = "images";

    @Override
    public void createProduct(LoginUser loginUser, ProductRequestDto request) {
        List<ProductImage> images = new ArrayList<>();
        for (MultipartFile image : request.getProductImages()) {
            if (!image.isEmpty()) {
                ImageValidator.validateImageFile(image);
                String datePath = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy/MM-dd"));
                String uploadFolder = UPLOAD_FOLDER + "/" + datePath + "/" + request.getCompany();
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
            .seller(loginUser.getMember())
            .tags(request.getTags())
            .productImages(images)
            .build();
        productRepository.save(product);
    }
}
