package com.example.library_management.controller;

import com.example.library_management.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * REST Controller for S3 file operations.
 * Base path: /api/files
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class S3Controller {

    private final S3Service s3Service;

    /**
     * Upload a file to S3.
     * 
     * @param file the file to upload
     * @return ResponseEntity with the uploaded file key
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("Received file upload request. Filename: {}, Size: {}", 
                file.getOriginalFilename(), file.getSize());
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "File is empty"));
        }
        
        try {
            String fileKey = s3Service.uploadFile(file);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "File uploaded successfully", "key", fileKey));
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }

    /**
     * Download a file from S3.
     * 
     * @param key the S3 key of the file to download
     * @return ResponseEntity with the file as InputStreamResource
     */
    @GetMapping("/download/{key}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String key) {
        log.info("Received file download request. Key: {}", key);
        return s3Service.downloadFile(key);
    }

    /**
     * Delete a file from S3.
     * 
     * @param key the S3 key of the file to delete
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/delete/{key}")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable String key) {
        log.info("Received file delete request. Key: {}", key);
        
        try {
            String message = s3Service.deleteFile(key);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete file: " + e.getMessage()));
        }
    }
}
