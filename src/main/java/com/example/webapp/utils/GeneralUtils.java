package com.example.webapp.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

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
        return file != null && !file.isEmpty();
    }
}
