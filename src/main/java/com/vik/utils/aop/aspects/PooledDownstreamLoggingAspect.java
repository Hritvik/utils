package com.vik.utils.aop.aspects;


import com.vik.utils.*;
import com.vik.utils.aop.annotations.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.tuple.*;
import org.apache.hc.core5.net.*;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;


@Slf4j
@Aspect
@Component
public class PooledDownstreamLoggingAspect {

    @Autowired
    LoggingMethods loggingMethods;

    @Around("@annotation(downstreamLog)")
    public Object logDownstreamCall(ProceedingJoinPoint joinPoint, PooledDownstreamLog downstreamLog) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String classMethodName = joinPoint.getTarget().getClass().getSimpleName() + "." + method.getName();

        Annotation[][] paramAnnotations = signature.getMethod().getParameterAnnotations();
        Object[] args = joinPoint.getArgs();
        Map<String, String> tags = new HashMap<>();
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation instanceof TagsParam && args[i] instanceof Map) {
                    tags.putAll((Map<String, String>) args[i]);
                    break;
                }
            }
        }

        Pair<String, String> uri = getUri(args, tags);
        loggingMethods.logDownstreamRequest(classMethodName, uri.getLeft(), uri.getRight(), args);
        Object response = joinPoint.proceed();
        loggingMethods.logDownstreamRequestResponse(classMethodName, uri.getLeft(), uri.getRight(), response, args);
        return response;
    }

    private Pair<String, String> getUri(Object[] args, Map<String, String> tags) {
        if (!tags.isEmpty()) {
            String host = tags.getOrDefault("host", "-");
            String path = tags.getOrDefault("path", "-");
            return Pair.of(host, path);
        }
        return Optional.ofNullable(args)
                .filter(a -> a.length > 0 && a[0] instanceof String)
                .map(a -> (String) a[0])
                .map(url -> {
                    try {
                        URIBuilder builder = new URIBuilder(url);
                        String host = Optional.ofNullable(builder.getHost()).orElse("-");
                        String path = Optional.ofNullable(builder.getPath()).filter(p -> !p.isEmpty()).orElse("-");
                        return Pair.of(host, path);
                    } catch (Exception e) {
                        return Pair.of("-", "-");
                    }
                })
                .orElse(Pair.of("-", "-"));
    }

}
