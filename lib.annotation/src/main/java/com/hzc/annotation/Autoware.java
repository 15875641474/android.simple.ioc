package com.hzc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by huang zong cheng on 2017/12/13.
 * 328854225@qq.com
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD})
public @interface Autoware {
    String[] params() default {};
}
