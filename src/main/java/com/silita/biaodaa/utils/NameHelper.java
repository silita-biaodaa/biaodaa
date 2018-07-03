package com.silita.biaodaa.utils;


import java.util.EmptyStackException;

/**
 * 命名辅助类,方便获取相应的name注解
 * Created by flym on 9/1/2016.
 */
public class NameHelper {

    /** 获取有name注解的类的value属性,如果value属性为空,则返回类名 */
    public static String getName(Class<?> clazz) {
        Name name = clazz.getAnnotation(Name.class);
        if(name == null)
            // TODO: 18/7/3  
            throw new EmptyStackException();

        String value = name.value();
        return ObjectUtils.isEmpty(value) ? clazz.getSimpleName() : value;
    }
}

