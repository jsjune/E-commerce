package com.productservice.usecase.impl;

import com.productservice.adapter.MemberClient;
import com.productservice.adapter.dto.MemberDto;
import com.productservice.entity.Seller;
import com.productservice.usecase.dto.RegisterProductDto;
import com.productservice.utils.AesUtil;
import com.productservice.utils.error.ErrorCode;
import com.productservice.utils.error.GlobalException;
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
import java.util.Optional;
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
    public void createProduct(Long memberId, RegisterProductDto command) throws Exception {
        MemberDto member = memberClient.getMemberInfo(memberId);
        if(member == null) {
            throw new GlobalException(ErrorCode.MEMBER_NOT_FOUND);
        }
        List<ProductImage> images = new ArrayList<>();
        for (MultipartFile image : command.productImages()) {
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

        Seller seller = Seller.builder()
            .sellerId(member.memberId())
            .company(member.company())
            .phoneNumber(aesUtil.aesEncode(member.phoneNumber()))
            .build();
        Product product = Product.builder()
            .name(command.name())
            .description(command.description())
            .price(command.price())
            .totalStock(command.stock())
            .soldQuantity(0L)
            .seller(seller)
            .tags(command.tags())
            .productImages(images)
            .build();
        productRepository.save(product);
    }

    @Override
    public int decreaseStock(Long productId, Long quantity) {
        Optional<Product> findProduct = productRepository.findById(productId);
        if (findProduct.isPresent()) {
            Product product = findProduct.get();
            if (product.getTotalStock() < quantity) {
                return -1;
            }
            product.decreaseStock(quantity);
            return 0;
        }
        return -1;
    }

    @Override
    public Boolean incrementStock(Long productId, Long quantity) {
        Optional<Product> findProduct = productRepository.findById(productId);
        if (findProduct.isPresent()) {
            Product product = findProduct.get();
            product.incrementStock(quantity);
            return true;
        }
        return null;
    }
}
