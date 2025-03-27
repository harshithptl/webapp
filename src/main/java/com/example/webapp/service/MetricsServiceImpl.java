package com.example.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;

@Component
public class MetricsServiceImpl implements MetricsService {

    private static final Logger logger = LoggerFactory.getLogger(MetricsServiceImpl.class);
    private static final String NAMESPACE = "webapp-metrics";

    private final CloudWatchClient cloudWatchClient;

    public MetricsServiceImpl(CloudWatchClient cloudWatchClient) {
        this.cloudWatchClient = cloudWatchClient;
    }

    @Override
    public void incrementCounter(String metricName) {
        logger.info("[METRIC] Counter incremented: {}", metricName);
        try {
            MetricDatum datum = MetricDatum.builder()
                    .metricName(metricName)
                    .value(1.0)
                    .unit(StandardUnit.COUNT)
                    .build();
            PutMetricDataRequest request = PutMetricDataRequest.builder()
                    .namespace(NAMESPACE)
                    .metricData(datum)
                    .build();
            cloudWatchClient.putMetricData(request);
        } catch (Exception e) {
            logger.error("Error sending counter metric {} to CloudWatch", metricName, e);
        }
    }

    @Override
    public void recordTimer(String metricName, long durationMs) {
        logger.info("[METRIC] Timer recorded: {} = {} ms", metricName, durationMs);
        try {
            MetricDatum datum = MetricDatum.builder()
                    .metricName(metricName)
                    .value((double) durationMs)
                    .unit(StandardUnit.MILLISECONDS)
                    .build();
            PutMetricDataRequest request = PutMetricDataRequest.builder()
                    .namespace(NAMESPACE)
                    .metricData(datum)
                    .build();
            cloudWatchClient.putMetricData(request);
        } catch (Exception e) {
            logger.error("Error sending timer metric {} to CloudWatch", metricName, e);
        }
    }
}
