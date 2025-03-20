package com.example.webapp.service;

import com.example.webapp.dao.S3FileMetadataDao;
import com.example.webapp.model.S3FileMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class S3FileServiceImpl implements S3FileService {

    private final S3FileMetadataDao s3FileMetadataDao;
    private final S3Client s3Client;

    @Value("${app.s3.bucket.name}")
    private String bucketName;

    public S3FileServiceImpl(S3FileMetadataDao fileRepo, S3Client s3Client) {
        this.s3FileMetadataDao = fileRepo;
        this.s3Client = s3Client;
    }

    @Override
    public S3FileMetadata uploadFile(MultipartFile multipartFile) throws IOException {
        String key = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try {
            s3Client.putObject(putRequest, RequestBody.fromBytes(multipartFile.getBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to upload file to S3", e);
        }

        S3FileMetadata fileMetadata = prepareS3FileMetadata(key);

        try {
            return s3FileMetadataDao.save(fileMetadata);
        } catch (Exception dbEx) {
            try {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build());
            } catch (Exception ignored) {
            }
            throw new IOException("Failed to save file metadata to database", dbEx);
        }
    }


    private S3FileMetadata prepareS3FileMetadata(String key) {
        S3FileMetadata fileMetadata = new S3FileMetadata();
        fileMetadata.setFileName(key);
        fileMetadata.setUrl(bucketName + "/" + key);
        fileMetadata.setUploadDate(LocalDate.now(Clock.systemUTC()));
        return fileMetadata;
    }

    @Override
    public Optional<S3FileMetadata> getFileMetadata(String id) {
        return s3FileMetadataDao.findById(id);
    }

    @Override
    public void deleteFile(String id) {
        S3FileMetadata metadata = s3FileMetadataDao.findById(id).orElseThrow();
        String url = metadata.getUrl();
        String key = url.substring(url.indexOf("/") + 1);

        DeleteObjectRequest delRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(delRequest);

        s3FileMetadataDao.deleteById(id);
    }
}
