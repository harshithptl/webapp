package com.example.webapp.controller;

import com.example.webapp.service.HealthCheckService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.example.webapp.utils.GeneralUtils.createResponse;

@RestController
@RequestMapping("/healthz")
public class HealthCheckController {

    @Autowired
    private HealthCheckService healthCheckService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Void> healthCheck(HttpServletRequest request) {
        try {
            return healthCheckService.healthCheck(request);
        } catch (Exception e) {
            return createResponse(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE,
            RequestMethod.PATCH, RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.TRACE})
    public ResponseEntity<Void> methodNotAllowed() {
        return createResponse(HttpStatus.METHOD_NOT_ALLOWED);
    }

}

