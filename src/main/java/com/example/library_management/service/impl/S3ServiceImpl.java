package com.example.library_management.service.impl;

import com.example.library_management.config.AwsS3Properties;
import com.example.library_management.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.InputStream;
import java.util.UUID;

/**
 * Implementation of S3Service for AWS S3 operations.
 * Handles file upload, download, and deletion operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final AwsS3Properties awsS3Properties;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // Generate unique filename using UUID
            String uniqueKey = UUID.randomUUID().toString() + fileExtension;
            
            log.info("Uploading file to S3. Key: {}, Bucket: {}", uniqueKey, awsS3Properties.getBucket());
            
            // Use RequestBody.fromInputStream (NOT fromBytes)
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(awsS3Properties.getBucket())
                    .key(uniqueKey)
                    .contentType(file.getContentType())
                    .build();
            
            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            log.info("File uploaded successfully. Key: {}", uniqueKey);
            return uniqueKey;
            
        } catch (S3Exception e) {
            log.error("S3 error while uploading file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error while uploading file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadFile(String key) {
        try {
            log.info("Downloading file from S3. Key: {}, Bucket: {}", key, awsS3Properties.getBucket());
            
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(awsS3Properties.getBucket())
                    .key(key)
                    .build();
            
            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);
            GetObjectResponse response = responseInputStream.response();
            InputStream inputStream = responseInputStream;
            
            // Determine content type from response metadata
            String contentType = response.contentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            
            // Extract filename from key for Content-Disposition header
            String filename = key;
            if (key.contains("/")) {
                filename = key.substring(key.lastIndexOf("/") + 1);
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("attachment", filename);
            if (response.contentLength() != null) {
                headers.setContentLength(response.contentLength());
            }
            
            log.info("File downloaded successfully. Key: {}", key);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
                    
        } catch (NoSuchKeyException e) {
            log.error("File not found in S3. Key: {}", key);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (S3Exception e) {
            log.error("S3 error while downloading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Error while downloading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public String deleteFile(String key) {
        try {
            log.info("Deleting file from S3. Key: {}, Bucket: {}", key, awsS3Properties.getBucket());
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(awsS3Properties.getBucket())
                    .key(key)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            
            log.info("File deleted successfully. Key: {}", key);
            return "File deleted successfully: " + key;
            
        } catch (S3Exception e) {
            log.error("S3 error while deleting file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete file from S3: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error while deleting file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }
}
