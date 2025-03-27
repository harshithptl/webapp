package com.example.webapp.controller;

import com.example.webapp.annotations.Counted;
import com.example.webapp.annotations.Timed;
import com.example.webapp.model.S3FileMetadata;
import com.example.webapp.service.S3FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(S3FileController.class);
    private final S3FileService s3FileService;

    public S3FileController(S3FileService s3FileService) {
        this.s3FileService = s3FileService;
    }

    @Counted("s3File.addFile.count")
    @Timed("s3File.addFile.latency")
    @RequestMapping(method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<?> addFile(@RequestParam("profilePic") MultipartFile file) {
        logger.info("addFile called");
        if (!validateFile(file)) {
            logger.error("Invalid file provided");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            S3FileMetadata metadata = s3FileService.uploadFile(file);
            logger.info("File uploaded successfully");
            return createResponse(HttpStatus.CREATED, metadata);
        } catch (IOException e) {
            logger.error("Failed to upload file", e);
            return createResponse(HttpStatus.SERVICE_UNAVAILABLE, "Failed to upload file.");
        }
    }

    @Counted("s3File.methodNotAllowedBase.count")
    @Timed("s3File.methodNotAllowedBase.latency")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE,
            RequestMethod.PATCH, RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> methodNotAllowedBase() {
        logger.info("methodNotAllowedBase called");
        return createResponse(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Counted("s3File.getFile.count")
    @Timed("s3File.getFile.latency")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getFile(HttpServletRequest httpServletRequest, @PathVariable String id) {
        logger.info("getFile called for id: {}", id);
        if (validateRequestBody(httpServletRequest)) {
            logger.error("Invalid request body in getFile");
            return createResponse(HttpStatus.BAD_REQUEST);
        }
        Optional<S3FileMetadata> metadata = s3FileService.getFileMetadata(id);
        if (metadata.isPresent()) {
            logger.info("File metadata found for id: {}", id);
            return createResponse(HttpStatus.OK, metadata.get());
        }
        logger.error("File metadata not found for id: {}", id);
        return createResponse(HttpStatus.NOT_FOUND);
    }

    @Counted("s3File.deleteFile.count")
    @Timed("s3File.deleteFile.latency")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteFile(HttpServletRequest httpServletRequest, @PathVariable String id) {
        logger.info("deleteFile called for id: {}", id);
        if (validateRequestBody(httpServletRequest)) {
            logger.error("Invalid request body in deleteFile");
            return createResponse(HttpStatus.BAD_REQUEST);
        }
        try {
            s3FileService.deleteFile(id);
            logger.info("File deleted successfully for id: {}", id);
            return createResponse(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error deleting file for id: {}", id, e);
            return createResponse(HttpStatus.NOT_FOUND);
        }
    }

    private boolean validateRequestBody(HttpServletRequest request) {
        String contentLength = request.getHeader("Content-Length");
        return contentLength != null && Integer.parseInt(contentLength) > 0;
    }

    @Counted("s3File.methodNotAllowedForId.count")
    @Timed("s3File.methodNotAllowedForId.latency")
    @RequestMapping(value = "/{id}", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH,
            RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> methodNotAllowedForId(@PathVariable String id) {
        logger.info("methodNotAllowedForId called for id: {}", id);
        return createResponse(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
