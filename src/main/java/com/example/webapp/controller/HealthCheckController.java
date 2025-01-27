package com.example.webapp.controller;

import com.example.webapp.model.HealthCheck;
import com.example.webapp.dao.HealthCheckDao;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.ZoneOffset;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/healthz")
public class HealthCheckController {

    private final HealthCheckDao healthCheckDao;

    public HealthCheckController(HealthCheckDao healthCheckDao) {
        this.healthCheckDao = healthCheckDao;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Void> healthCheck() {
        try {
            HealthCheck healthCheck = new HealthCheck();
            healthCheck.setDatetime(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
            healthCheckDao.save(healthCheck);
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("X-Content-Type-Options", "nosniff")
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("X-Content-Type-Options", "nosniff")
                    .build();
        }
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE,
            RequestMethod.PATCH, RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.TRACE})
    public ResponseEntity<Void> methodNotAllowed() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff")
                .build();
    }
}

