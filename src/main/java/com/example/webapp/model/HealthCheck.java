package com.example.webapp.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "health_check")
public class HealthCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long checkId;

    @Column(nullable = false)
    private LocalDateTime datetime;
}
