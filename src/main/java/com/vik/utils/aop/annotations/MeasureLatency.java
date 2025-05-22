package com.vik.utils.aop.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MeasureLatency {
    String metricName() default "";
    String[] tags() default {};
}