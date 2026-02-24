package com.example.library_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS S3 Configuration class.
 * Creates S3Client bean configured for ap-south-1 region.
 * Uses default credentials provider (IAM Role when running on EC2).
 */
@Configuration
public class S3Config {

    private static final Region AWS_REGION = Region.AP_SOUTH_1;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(AWS_REGION)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
