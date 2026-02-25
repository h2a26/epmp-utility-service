package org.mpay.utilityservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GarageS3Service {

    private final S3Client s3Client;

    @Value("${storage.local-temp-path}")
    private String tempFolderPath;

    @Value("${garage.bucket}")
    private String bucket;

    @Value("${garage.electricitybill_dir}")
    private String directory;

    public String uploadToGarage(MultipartFile file) throws Exception {
        String fileName = String.format("%s/%s_%s", directory, UUID.randomUUID(), file.getOriginalFilename());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return fileName;
    }

    public String downloadFromGarage(String s3Key) throws Exception {
        // 1. Resolve and create the directory
        Path folderPath = Paths.get(tempFolderPath).toAbsolutePath().normalize();
        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
            log.info("Created local temp directory at: {}", folderPath);
        }

        // 2. Create a unique filename to prevent collisions during concurrent uploads
        String simpleName = s3Key.substring(s3Key.lastIndexOf("/") + 1);
        Path targetPath = folderPath.resolve(simpleName);

        log.info("Downloading S3 object to local path: {}", targetPath);

        // 3. Perform the download
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .build();

        s3Client.getObject(getObjectRequest, targetPath);

        return targetPath.toString();
    }
}