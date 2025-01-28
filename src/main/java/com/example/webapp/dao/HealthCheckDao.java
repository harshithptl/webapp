package com.example.webapp.dao;

import com.example.webapp.model.HealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthCheckDao extends JpaRepository<HealthCheck, Long> {
}
