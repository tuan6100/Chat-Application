package com.chat.app.service.aws;

import com.chat.app.security.MySecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final Region region;


    public S3Service(@Value ("${spring.cloud.aws.bucket-name}") String bucketName) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(MySecretKey.AWS_ACCESS_KEY_ID, MySecretKey.AWS_SECRET_ACCESS_KEY);
        this.bucketName = bucketName;
        this.region = Region.AP_SOUTHEAST_2;
        this.s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename(), "File name is null");
        String fileName = UUID.randomUUID() + "_" + originalFileName;
        Path tempFilePath = Files.createTempFile("upload-", originalFileName);
        try {
            Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(URLConnection.guessContentTypeFromName(fileName))
                    .build();
            PutObjectResponse response = s3Client.putObject(putObjectRequest, tempFilePath);
            if (response == null || response.sdkHttpResponse() == null || !response.sdkHttpResponse().isSuccessful()) {
                throw new RuntimeException("Failed to upload file to S3");
            }
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region.id(), fileName);

        } finally {
            Files.deleteIfExists(tempFilePath);
        }
    }

    public byte[] downloadFile(String fileKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file: " + e.getMessage(), e);
        }
    }

    public String deleteFile(String fileUrl) {
        try {
            String fileKey = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileKey)
                            .build()
            );
            return "File deleted successfully";
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }
}