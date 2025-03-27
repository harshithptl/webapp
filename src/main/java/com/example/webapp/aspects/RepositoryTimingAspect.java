package com.example.webapp.aspects;

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
public class RepositoryTimingAspect {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryTimingAspect.class);

    @Autowired
    private MetricsService metricsService;

    @Around("execution(* com.example.webapp.dao..*(..))")
    public Object profileRepositoryMethods(ProceedingJoinPoint pjp) throws Throwable {
        String metricName = "repository." + pjp.getSignature().toShortString();
        TimerUtils timer = TimerUtils.start();
        try {
            return pjp.proceed();
        } finally {
            long elapsedMillis = timer.elapsedMillis();
            metricsService.recordTimer(metricName + ".latency", elapsedMillis);
            logger.info("Repository method {} executed in {} ms", metricName, elapsedMillis);
        }
    }
}
