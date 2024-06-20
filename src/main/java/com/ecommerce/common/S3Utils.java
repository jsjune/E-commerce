package com.ecommerce.common;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class S3Utils {
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket-name}")
    public String bucket;

    public String uploadFile(MultipartFile file, String s3Path) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, s3Path, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
            return amazonS3Client.getUrl(bucket, s3Path).toString();

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 실패");
        }
    }

    public String uploadThumbFile(MultipartFile file, String thumbS3Path) {
        try {
            BufferedImage bufferImage = ImageIO.read(file.getInputStream());
            BufferedImage thumbnailImage = Thumbnails.of(bufferImage).size(400, 333).asBufferedImage();

            ByteArrayOutputStream thumbOutput = new ByteArrayOutputStream();
            String imageType = file.getContentType();
            ImageIO.write(thumbnailImage, imageType.substring(imageType.indexOf("/") + 1), thumbOutput);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            byte[] thumbBytes = thumbOutput.toByteArray();
            objectMetadata.setContentLength(thumbBytes.length);
            objectMetadata.setContentType(file.getContentType());

            InputStream thumbStream = new ByteArrayInputStream(thumbBytes);
            amazonS3Client.putObject(new PutObjectRequest(bucket, thumbS3Path, thumbStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
            return amazonS3Client.getUrl(bucket, thumbS3Path).toString();

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 실패");
        }
    }

    public void deleteFile(String s3Path) {
        amazonS3Client.deleteObject(bucket, s3Path);
    }

}
