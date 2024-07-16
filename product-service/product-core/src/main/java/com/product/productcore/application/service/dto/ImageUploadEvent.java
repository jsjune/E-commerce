package com.product.productcore.application.service.dto;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record ImageUploadEvent(
    List<byte[]> productImages,
    List<String> fileNames,
    List<String> contentTypes,
    String company,
    Long productId) {

    public ImageUploadEvent(List<MultipartFile> images, String company, Long productId) {
        this(
            images.stream()
                .map(image -> {
                    try {
                        return image.getBytes();
                    } catch (IOException e) {
                        throw new RuntimeException("Error while reading image bytes: " + image.getOriginalFilename(), e);
                    }
                })
                .toList(),
            images.stream()
                .map(MultipartFile::getOriginalFilename)
                .toList(),
            images.stream()
                .map(MultipartFile::getContentType)
                .toList(),
            company,
            productId
        );
    }

}
