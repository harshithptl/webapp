package com.example.webapp.service;

public interface MetricsService {
    void incrementCounter(String metricName);

    void recordTimer(String metricName, long durationMs);
}
