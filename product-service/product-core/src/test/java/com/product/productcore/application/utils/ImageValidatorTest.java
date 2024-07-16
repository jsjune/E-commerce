package com.product.productcore.application.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.product.productapi.common.error.ErrorCode;
import com.product.productapi.common.error.GlobalException;
import com.product.productcore.testConfig.IntegrationTestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class ImageValidatorTest extends IntegrationTestSupport {

    @DisplayName("이미지 허용되지 않는 포멧 시 에러 발생")
    @Test
    void test() {
        // given
        String originalFilename = "image";
        MockMultipartFile multipartFile = new MockMultipartFile("image", originalFilename, "image/jpeg",
            "image".getBytes());

        // when then
        GlobalException exception = assertThrows(GlobalException.class,
            () -> ImageValidator.validateImageFile(multipartFile));
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.INVALID_FILE_FORMAT);

    }

    @DisplayName("이미지 허용되지 않는 MIME 타입 시 에러 발생")
    @Test
    void unsupported_MIME_type() {
        // given
        String contentType = "text/html";
        MockMultipartFile multipartFile = new MockMultipartFile("image", "image.jpg", contentType,
            "image".getBytes());

        // when then
        GlobalException exception = assertThrows(GlobalException.class,
            () -> ImageValidator.validateImageFile(multipartFile));
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.UNSUPPORTED_MIME_TYPE);

    }

    @DisplayName("이미지 허용되지 않는 확장자 시 에러 발생")
    @Test
    void invalid_file_extension() {
        // given
        String originalFilename = "image.svg";
        MockMultipartFile multipartFile = new MockMultipartFile("image", originalFilename,
            "image/jpeg", "image".getBytes());

        // when then
        GlobalException exception = assertThrows(GlobalException.class,
            () -> ImageValidator.validateImageFile(multipartFile));
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.INVALID_FILE_EXTENSION);
    }
}
