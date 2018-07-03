package com.silita.biaodaa.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.List;

/**
 * 数据转换工具类，用于将指定的类转换为另一个类
 * 使用spring conversionService进行基础转换工作
 *
 * @author flym
 */
@Slf4j
public class ConvertUtils {
    private static ConfigurableConversionService conversionService;

    private static ConfigurableConversionService getOrDefault() {
        if(conversionService == null) {
            conversionService = new DefaultConversionService();
            addImplementConverter(conversionService);
        }

        return conversionService;
    }

    /** 添加系统内置的转换器实现 */
    private static void addImplementConverter(ConfigurableConversionService service) {
        List<Class<? extends Converter>> converterClassList = ClassFinder.getInstance().findSubClass(Converter.class);
        List<Converter> converterList = ClassUtils.newInstance(converterClassList, true);
        converterList.forEach(t -> {
            try{
                addConverter(t);
            } catch(TypeNotPresentException e) {
                log.debug("加载转换器:{},失败:{},忽略之", t.getClass().getName(), e.getMessage());
            }
        });
    }

    /** 设置相应的转换服务 */
    public static void setConversionService(ConfigurableConversionService conversionService) {
        ConvertUtils.conversionService = conversionService;
        addImplementConverter(ConvertUtils.conversionService);
    }

    /** 是否能进行相应类型的转换 */
    public static boolean canConvert(Class<?> sourceType, Class<?> destType) {
        return getOrDefault().canConvert(sourceType, destType);
    }

    /** 将指定源数据转换为目标类型数据 */
    public static <D, S> D convert(S s, Class<D> destType) {
        return getOrDefault().convert(s, destType);
    }

    /** 添加转换器 */
    public static <D, S> void addConverter(Converter<S, D> converter) {
        getOrDefault().addConverter(converter);
    }
}
