package com.product.productcore.application.utils;

import com.product.productapi.common.error.ErrorCode;
import com.product.productapi.common.error.GlobalException;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class ImageValidator {
    private static final List<String> ALLOWED_FILE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif");

    public static void validateImageFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.contains(".")) {
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if (!ALLOWED_FILE_EXTENSIONS.contains(fileExtension)) {
                throw new GlobalException(ErrorCode.INVALID_FILE_EXTENSION);
            }
        } else {
            throw new GlobalException(ErrorCode.INVALID_FILE_FORMAT);
        }

        String mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new GlobalException(ErrorCode.UNSUPPORTED_MIME_TYPE);
        }
    }
}
