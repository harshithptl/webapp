package com.example.webapp.aspects;

import com.example.webapp.annotations.Counted;
import com.example.webapp.service.MetricsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CountingAspect {

    private static final Logger logger = LoggerFactory.getLogger(CountingAspect.class);

    @Autowired
    private MetricsService metricsService;

    @Around("@annotation(counted)")
    public Object count(ProceedingJoinPoint pjp, Counted counted) throws Throwable {
        String metricName = counted.value();
        if (metricName.isEmpty()) {
            metricName = pjp.getSignature().toShortString();
        }
        metricsService.incrementCounter(metricName + ".count");
        logger.info("Incremented counter for metric: {}", metricName);
        return pjp.proceed();
    }
}
