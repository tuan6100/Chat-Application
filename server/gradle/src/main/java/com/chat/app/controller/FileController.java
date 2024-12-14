package com.chat.app.controller;

import com.chat.app.service.aws.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
public class FileController {

    private final S3Service s3Service;

    @Autowired
    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = s3Service.uploadFile(file);
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        try {
            String message = s3Service.deleteFile(fileUrl);
            return ResponseEntity.ok().body(message);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete file: " + e.getMessage());
        }
    }

}
