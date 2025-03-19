package com.example.webapp.service;

import com.example.webapp.model.S3FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface S3FileService {
    S3FileMetadata uploadFile(MultipartFile multipartFile) throws IOException;

    Optional<S3FileMetadata> getFileMetadata(String id);

    void deleteFile(String id);
}
