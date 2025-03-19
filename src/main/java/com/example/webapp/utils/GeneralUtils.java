package com.example.webapp.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class GeneralUtils {
    public static ResponseEntity<Void> createResponse(HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff")
                .build();
    }

    public static <T> ResponseEntity<T> createResponse(HttpStatus httpStatus, T body) {
        return ResponseEntity.status(httpStatus)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff")
                .body(body);
    }

    public static boolean validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        return validateExtension(file);
    }

    public static boolean validateExtension(MultipartFile file) {
        List<String> allowedExtensions = Arrays.asList("png", "jpg", "jpeg", "gif");

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            return false;
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1)
                .toLowerCase();

        return allowedExtensions.contains(extension);
    }
}
