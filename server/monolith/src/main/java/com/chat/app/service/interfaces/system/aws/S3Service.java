package com.chat.app.service.interfaces.system.aws;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface S3Service {

    String uploadFile(MultipartFile file) throws IOException;

    byte[] downloadFile(String fileUrl);

    Long getFileSize(String fileUrl);

    String deleteFile(String fileUrl);

}
