package com.example.webapp.dao;

import com.example.webapp.model.S3FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface S3FileMetadataDao extends JpaRepository<S3FileMetadata, String> {
}
