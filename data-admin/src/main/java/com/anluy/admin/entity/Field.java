package com.anluy.admin.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 功能说明：自定义字段注解
 * <p>
 * Created by hc.zeng on 2018/10/9.
 */
@Target({ FIELD})
@Retention(RUNTIME)
public @interface Field {
    String value() default "";
}
