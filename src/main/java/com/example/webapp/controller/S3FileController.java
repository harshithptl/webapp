package com.example.webapp.controller;

import com.example.webapp.model.S3FileMetadata;
import com.example.webapp.service.S3FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.example.webapp.utils.GeneralUtils.createResponse;
import static com.example.webapp.utils.GeneralUtils.validateFile;

@RestController
@RequestMapping("/file")
public class S3FileController {

    private final S3FileService s3FileService;

    public S3FileController(S3FileService s3FileService) {
        this.s3FileService = s3FileService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<?> addFile(@RequestParam("profilePic") MultipartFile file) {
        if (!validateFile(file)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            S3FileMetadata metadata = s3FileService.uploadFile(file);
            return createResponse(HttpStatus.CREATED, metadata);
        } catch (IOException e) {
            return createResponse(HttpStatus.BAD_REQUEST, "Failed to upload file.");
        }
    }

    @RequestMapping(method = {
            RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE,
            RequestMethod.PATCH, RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> methodNotAllowedBase() {
        return createResponse(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getFile(HttpServletRequest httpServletRequest,  @PathVariable String id) {
        if (validateRequestBody(httpServletRequest)) {
            return createResponse(HttpStatus.BAD_REQUEST);
        }
        Optional<S3FileMetadata> metadata = s3FileService.getFileMetadata(id);
        if (metadata.isPresent()) {
            return createResponse(HttpStatus.OK, metadata.get());
        }
        return createResponse(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteFile(HttpServletRequest httpServletRequest, @PathVariable String id) {
        if (validateRequestBody(httpServletRequest)) {
            return createResponse(HttpStatus.BAD_REQUEST);
        }
        try {
            s3FileService.deleteFile(id);
            return createResponse(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return createResponse(HttpStatus.NOT_FOUND);
        }
    }

    private boolean validateRequestBody(HttpServletRequest request) {
        String contentLength = request.getHeader("Content-Length");
        return contentLength != null && Integer.parseInt(contentLength) > 0;
    }

    @RequestMapping(value = "/{id}", method = {
            RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH,
            RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> methodNotAllowedForId(@PathVariable String id) {
        return createResponse(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
