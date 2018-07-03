package com.silita.biaodaa.utils;

import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

/** @author flym */
@Component
public class ApplicationContextHolder implements BeanFactoryPostProcessor, ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static Map<Class<?>, String> nameMap = Maps.newHashMap();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //nothing to do
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        return getBean(clazz, () -> {
            throw new RuntimeException("类:" + clazz + "没有相应的spring定义");
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz, Supplier<T> ifAbsent) {
        String beanName = nameMap.get(clazz);
        if (beanName == null) {
            String[] beanNames = applicationContext.getBeanNamesForType(clazz);
            if (beanNames.length == 0)
                return ifAbsent.get();
            if (beanNames.length > 1)
                throw new RuntimeException("类:" + clazz + "有多个bean定义:" + Arrays.toString(beanNames));
            beanName = beanNames[0];
            nameMap.put(clazz, beanName);
        }

        return (T) applicationContext.getBean(beanName);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        return (T) applicationContext.getBean(beanName);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName, Supplier<T> ifAbsent) {
        if (!applicationContext.containsBean(beanName))
            return ifAbsent.get();

        return (T) applicationContext.getBean(beanName);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
