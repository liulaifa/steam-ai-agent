package com.baozi.steamedCommon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)           // 用在方法上
@Retention(RetentionPolicy.RUNTIME)   // 运行时保留
public @interface Log {

    /**
     * 操作内容
     */
    String value();
}
