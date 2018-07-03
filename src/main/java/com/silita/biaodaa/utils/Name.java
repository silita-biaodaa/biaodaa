package com.silita.biaodaa.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述一个组件的命名服务
 * 或者是一个信息的描述
 * 命名服务可以通过 / 分隔来描述指定的分类组件信息,如 e/ysf
 *
 * @author flym
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Name {
    /** 该任务的名称值,如果没有,则使用相应的类名作为名称 */
    String value() default "";

    /** 该组件的描述信息值 */
    String desc() default "";
}