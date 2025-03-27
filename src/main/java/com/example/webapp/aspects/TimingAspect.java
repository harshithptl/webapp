package com.example.webapp.aspects;

import com.example.webapp.annotations.Timed;
import com.example.webapp.service.MetricsService;
import com.example.webapp.utils.TimerUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimingAspect {

    private static final Logger logger = LoggerFactory.getLogger(TimingAspect.class);

    @Autowired
    private MetricsService metricsService;

    @Around("@annotation(timed)")
    public Object time(ProceedingJoinPoint pjp, Timed timed) throws Throwable {
        String metricName = timed.value();
        if (metricName.isEmpty()) {
            metricName = pjp.getSignature().toShortString();
        }
        TimerUtils timer = TimerUtils.start();
        Object result;
        try {
            result = pjp.proceed();
        } finally {
            long elapsedMillis = timer.elapsedMillis();
            metricsService.recordTimer(metricName + ".latency", elapsedMillis);
            logger.info("Recorded timer for metric: {} with elapsed time: {} ms", metricName, elapsedMillis);
        }
        return result;
    }
}
