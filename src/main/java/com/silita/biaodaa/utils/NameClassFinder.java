package com.silita.biaodaa.utils;

import com.google.common.base.Functions;

import java.util.List;
import java.util.Map;

/**
 * 命名服务与类之间的相互映射关系
 *
 * @author flym
 */
public class NameClassFinder {
    private static NameClassFinder instance;

    private Map<String, Class> nameMap;

    private NameClassFinder() {
        init();
    }

    private void init() {
        @SuppressWarnings("unchecked")
        List<Class> classList = (List) ClassFinder.getInstance().findClassByAnnotation(Name.class);

        nameMap = MapUtils.asMap(classList, NameHelper::getName, Functions.identity());
    }

    public static NameClassFinder getInstance() {
        if(instance == null)
            instance = new NameClassFinder();

        return instance;
    }

    /** 根据命名获取相应的类信息,如果不能获取,则抛出相应的异常 */
    @SuppressWarnings("unchecked")
    public <T> Class<T> get(String name) {
        Class<T> result = nameMap.get(name);
        if(result == null) {
            result = ClassUtils.forName(name);
        }

        return result;
    }
}
