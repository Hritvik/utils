package com.vik.utils.aop.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ControlleraInstrumentation {

    String apiPath();
}
