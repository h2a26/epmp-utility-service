package org.mpay.utilityservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${garage.access_key}")
    private String accessKey;

    @Value("${garage.secret_key}")
    private String secretKey;

    @Value("${garage.host}")
    private String host;

    @Value("${garage.port}")
    private int port;

    @Bean
    public S3Client s3Client() {
        String endpoint = String.format("http://%s:%d", host, port);

        return S3Client.builder()
                .region(Region.of("garage"))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}