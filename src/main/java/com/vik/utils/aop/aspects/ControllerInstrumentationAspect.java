package com.vik.utils.aop.aspects;


import com.vik.utils.*;
import com.vik.utils.aop.annotations.*;
import com.vik.utils.data.responses.*;
import com.vik.utils.exceptions.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.exception.*;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ControllerInstrumentationAspect {

    @Autowired
    private final MetricsService metricsService;

    @Around("@annotation(controlleraInstrumentation)")
    public Object logAndMetrics(
            ProceedingJoinPoint joinPoint, ControlleraInstrumentation controlleraInstrumentation)
            throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String apiPath = controlleraInstrumentation.apiPath();
        Object[] args = joinPoint.getArgs();

        log.info("Request to API: {}, Method: {} with arguments: {}", apiPath, methodName, args);
        metricsService.incrementCounter("Controller_Request", "api", apiPath);
        try {
            Object result = joinPoint.proceed();
            log.info(
                    "Response from API: {}, Method: {}. Result: {}, arguments: {}",
                    apiPath,
                    methodName,
                    result,
                    args);
            return result;
        } catch (BadRequestException e) {
            logError(apiPath, methodName, args, e);
            BaseResponse errorResponse = new BaseResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            logError(apiPath, methodName, args, e);
            BaseResponse errorResponse = new BaseResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private void logError(
            String apiPath, String methodName, Object[] args, Exception e) {
        log.error(
                "Exception in API {} ({}): Arguments: {} - Error: {}",
                apiPath,
                methodName,
                args,
                ExceptionUtils.getStackTrace(e));
        metricsService.incrementCounter("Controller_Exception", "api", apiPath, "error", e.getClass().getSimpleName());
    }
}
