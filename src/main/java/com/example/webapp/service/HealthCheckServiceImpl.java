package com.example.webapp.service;

import com.example.webapp.dao.HealthCheckDao;
import com.example.webapp.model.HealthCheck;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static com.example.webapp.utils.GeneralUtils.createResponse;

@Service
public class HealthCheckServiceImpl implements HealthCheckService {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckServiceImpl.class);
    private final HealthCheckDao healthCheckDao;

    public HealthCheckServiceImpl(HealthCheckDao healthCheckDao) {
        this.healthCheckDao = healthCheckDao;
    }

    @Override
    public ResponseEntity<Void> healthCheck(HttpServletRequest request) {
        logger.info("Received health check request: {}", request.getRequestURI());
        if (!validateRequest(request)) {
            logger.warn("Health check request validation failed");
            return createResponse(HttpStatus.BAD_REQUEST);
        }
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setDatetime(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        healthCheckDao.save(healthCheck);
        logger.info("Health check record saved at {}", healthCheck.getDatetime());
        return createResponse(HttpStatus.OK);
    }

    private boolean validateRequest(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap != null && !parameterMap.isEmpty()) {
            return false;
        }
        String contentLength = request.getHeader("Content-Length");
        if (contentLength != null && Integer.parseInt(contentLength) > 0) {
            return false;
        }
        return true;
    }
}
