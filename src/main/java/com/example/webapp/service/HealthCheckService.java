package com.example.webapp.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface HealthCheckService {
    ResponseEntity<Void> healthCheck(HttpServletRequest request);
}
