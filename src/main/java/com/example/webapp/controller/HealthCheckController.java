package com.example.webapp.controller;

import com.example.webapp.annotations.Counted;
import com.example.webapp.annotations.Timed;
import com.example.webapp.service.HealthCheckService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.example.webapp.utils.GeneralUtils.createResponse;

@RestController
@RequestMapping("/healthz")
public class HealthCheckController {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);
    private final HealthCheckService healthCheckService;

    public HealthCheckController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @Counted("healthCheck.count")
    @Timed("healthCheck.latency")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Void> healthCheck(HttpServletRequest request) {
        logger.info("Health check API called with request: {}", request);
        try {
            return healthCheckService.healthCheck(request);
        } catch (Exception e) {
            logger.error("Error in healthCheck", e);
            return createResponse(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @Counted("healthCheck.methodNotAllowed.count")
    @Timed("healthCheck.methodNotAllowed.latency")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE,
            RequestMethod.PATCH, RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.TRACE})
    public ResponseEntity<Void> methodNotAllowed() {
        logger.info("Method not allowed for health check endpoint");
        return createResponse(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
