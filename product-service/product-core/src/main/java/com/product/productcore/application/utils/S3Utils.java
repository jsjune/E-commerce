package com.product.productcore.application.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

//@Component
@RequiredArgsConstructor
@Slf4j
public class S3Utils {
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket-name}")
    public String bucket;

    public String uploadFile(InputStream inputStream, long contentLength, String contentType, String s3Path) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(contentLength);
        objectMetadata.setContentType(contentType);

        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, s3Path, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
            return amazonS3Client.getUrl(bucket, s3Path).toString();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 실패");
        }
    }

    public String uploadThumbFile(InputStream inputStream, String contentType, String thumbS3Path) {
        try {
            BufferedImage bufferImage = ImageIO.read(inputStream);
            BufferedImage thumbnailImage = Thumbnails.of(bufferImage).size(400, 333).asBufferedImage();

            ByteArrayOutputStream thumbOutput = new ByteArrayOutputStream();
            ImageIO.write(thumbnailImage, contentType.substring(contentType.indexOf("/") + 1), thumbOutput);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            byte[] thumbBytes = thumbOutput.toByteArray();
            objectMetadata.setContentLength(thumbBytes.length);
            objectMetadata.setContentType(contentType);

            InputStream thumbStream = new ByteArrayInputStream(thumbBytes);
            amazonS3Client.putObject(new PutObjectRequest(bucket, thumbS3Path, thumbStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
            return amazonS3Client.getUrl(bucket, thumbS3Path).toString();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 실패");
        }
    }

    public void deleteFile(String s3Path) {
        amazonS3Client.deleteObject(bucket, s3Path);
    }
}
