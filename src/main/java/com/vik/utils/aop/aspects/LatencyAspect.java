package com.vik.utils.aop.aspects;

import com.vik.utils.*;
import com.vik.utils.aop.annotations.*;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.*;
import org.springframework.stereotype.*;

@Aspect
@Component
public class LatencyAspect {

    private final MetricsService metricsService;

    public LatencyAspect(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Around("@annotation(com.vik.herald.annotation.MeasureLatency)")
    public Object measureLatency(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        MeasureLatency annotation = signature.getMethod().getAnnotation(MeasureLatency.class);

        String metricName = annotation.metricName();
        if (metricName.isEmpty()) {
            metricName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        }

        return metricsService.recordDuration(metricName, () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }, annotation.tags());
    }
} 