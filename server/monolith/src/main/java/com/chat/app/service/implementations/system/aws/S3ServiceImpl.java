package com.chat.app.service.implementations.system.aws;

import com.chat.app.security.MySecretKey;
import com.chat.app.service.interfaces.system.aws.S3Service;
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
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final Region region = Region.AP_SOUTHEAST_2;


    private S3ServiceImpl(@Value ("${spring.cloud.aws.bucket-name}") String bucketName) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(MySecretKey.AWS_ACCESS_KEY_ID, MySecretKey.AWS_SECRET_ACCESS_KEY);
        this.bucketName = bucketName;
        this.s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    @Override
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

    @Override
    public byte[] downloadFile(String fileUrl) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileUrl)
                    .build();
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file: " + e.getMessage(), e);
        }
    }

    @Override
    public Long getFileSize(String fileUrl) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileUrl)
                    .build();
            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            return headObjectResponse.contentLength();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file size: " + e.getMessage(), e);
        }
    }

    @Override
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