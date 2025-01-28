package com.example.webapp.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GeneralUtils {
    public static ResponseEntity<Void> createResponse(HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff")
                .build();
    }
}
