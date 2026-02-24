package com.example.library_management.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for AWS S3 operations.
 */
public interface S3Service {
    
    /**
     * Uploads a file to S3 bucket.
     * 
     * @param file the file to upload
     * @return the S3 key (unique filename) of the uploaded file
     */
    String uploadFile(MultipartFile file);
    
    /**
     * Downloads a file from S3 bucket.
     * 
     * @param key the S3 key of the file to download
     * @return ResponseEntity containing the file as InputStreamResource
     */
    ResponseEntity<InputStreamResource> downloadFile(String key);
    
    /**
     * Deletes a file from S3 bucket.
     * 
     * @param key the S3 key of the file to delete
     * @return success message
     */
    String deleteFile(String key);
}
