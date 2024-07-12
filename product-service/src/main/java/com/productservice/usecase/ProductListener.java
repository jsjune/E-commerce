package com.productservice.usecase;

import com.productservice.entity.Product;
import com.productservice.entity.ProductImage;
import com.productservice.repository.ProductRepository;
import com.productservice.usecase.dto.ImageUploadEvent;
import com.productservice.utils.S3Utils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductListener {

    private final S3Utils s3Utils;
    private final ProductRepository productRepository;
    private static final String UPLOAD_FOLDER = "images";

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listenImageUpload(ImageUploadEvent event) {
        Optional<Product> findProduct = productRepository.findById(event.productId());
        if (findProduct.isPresent()) {
            Product product = findProduct.get();
            List<ProductImage> images = new ArrayList<>();
            List<byte[]> imageBytesList = event.productImages();
            List<String> fileNames = event.fileNames();
            List<String> contentTypes = event.contentTypes();
            try {
                for (int i = 0; i < imageBytesList.size(); i++) {
                    byte[] imageBytes = imageBytesList.get(i);
                    String fileName = fileNames.get(i);
                    String contentType = contentTypes.get(i);

                    InputStream inputStream = new ByteArrayInputStream(imageBytes);
                    InputStream thumbInputStream = new ByteArrayInputStream(imageBytes);

                    String datePath = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy/MM-dd"));
                    String uploadFolder = UPLOAD_FOLDER + "/" + datePath + "/" + event.company();
                    String uploadImageName = UUID.randomUUID() + "_" + fileName;

                    String s3Path = uploadFolder + "/" + uploadImageName;
                    String orgImageUrl = s3Utils.uploadFile(inputStream, imageBytes.length,
                        contentType, s3Path);
                    String thumbS3Path = uploadFolder + "/s_" + uploadImageName;
                    String thumbnailUrl = s3Utils.uploadThumbFile(thumbInputStream, contentType,
                        thumbS3Path);
                    images.add(new ProductImage(orgImageUrl, s3Path, thumbnailUrl, thumbS3Path));
                }
                product.assignImages(images);
                productRepository.save(product);
            } catch (Exception e) {
                productRepository.delete(product);
                log.error("Failed to upload images: {}", e.getMessage(), e);
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        }
    }

}
