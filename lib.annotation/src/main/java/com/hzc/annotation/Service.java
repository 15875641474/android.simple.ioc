package com.hzc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by huang zong cheng on 2017/12/12.
 * 328854225@qq.com
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface Service {
}
