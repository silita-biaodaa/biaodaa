package com.silita.biaodaa.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/** @author flym */
@Slf4j
public class ClassUtils {

    /** class.forName的无异常版 */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> forName(String clazz) {
        try{
            return (Class<T>) Class.forName(clazz);
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /** 对指定的类进行初始化 */
    public static <T> T newInstance(Class<T> clazz) {
        try{
            return clazz.newInstance();
        } catch(IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /** 对批量的类进行初始化 */
    public static <T> List<T> newInstance(List<Class<? extends T>> clazzList) {
        return newInstance(clazzList, false);
    }

    /**
     * 对批量的类进行初始化
     *
     * @param ignoreNotFoundException 如果加载类失败时,是否忽略加载失败的类
     */
    public static <T> List<T> newInstance(List<Class<? extends T>> clazzList, boolean ignoreNotFoundException) {
        List<T> tList = Lists.newArrayList();
        for(Class<? extends T> clazz : clazzList) {
            try{
                T t = newInstance(clazz);
                tList.add(t);
            } catch(NoClassDefFoundError error) {
                if(!ignoreNotFoundException)
                    throw error;
            }
        }
        return tList;
    }

    /** 将指定类型的对象向下转型为子类的对象 */
    @SuppressWarnings("unchecked")
    public static <S, T extends S> T subInstance(S s, Class<T> clazz) {
        if(s == null)
            return null;

        //Asserts.assertTrue(s.getClass().isAssignableFrom(clazz), "目标类{}不是当前对象类{}的子类", clazz, s.getClass());

        if(clazz == s.getClass()) {
            return (T) s;
        }
        T t = newInstance(clazz);
        try{
            BeanUtils.copyProperties(s, t);
        } catch(BeansException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return t;
    }

    /** 将指定类型的对象copy为另一个类的对象,按照属性名进行对称copy,如果可以进行转换，则进行可能的转换 */
    public static <T, S> T copyInstance(S s, Class<T> clazz) {
        Map<String, Object> map = Maps.newHashMap();
        @SuppressWarnings("unchecked")
        Class<S> sourceClass = (Class<S>) s.getClass();
        try{
            PropertyDescriptor[] sourceDescriptors = BeanUtils.getPropertyDescriptors(sourceClass);
            for(PropertyDescriptor descriptor : sourceDescriptors) {
                Method readMethod = descriptor.getReadMethod();
                if(readMethod == null)
                    continue;
                map.put(descriptor.getName(), readMethod.invoke(s));
            }
        } catch(IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return copyInstance(map, clazz);
    }

    /** 使用map中的数据转换为另一个类的对象信息,按照属性名进行对称copy,如果可以进行转换，则进行可能的转换 */
    public static <T> T copyInstance(Map<String, ?> map, Class<T> clazz) {
        T t = newInstance(clazz);
        PropertyDescriptor[] destDescriptors = BeanUtils.getPropertyDescriptors(clazz);
        for(PropertyDescriptor descriptor : destDescriptors) {
            String property = descriptor.getName();

            //目标没有可写方法，则略过
            Method writeMethod = descriptor.getWriteMethod();
            if(writeMethod == null)
                continue;

            //源没有相应属性，略过
            if(!map.containsKey(property))
                continue;

            //源值为null，略过
            Object value = map.get(property);
            if(value == null)
                continue;

            Class<?> propertyType = descriptor.getPropertyType();
            Class<?> valueClazz = value.getClass();

            Object convertValue;
            //如果本身就是类型相同的，则不需要进行转换
            if(propertyType.isAssignableFrom(valueClazz)) {
                convertValue = value;
            } else {
                convertValue = ConvertUtils.convert(value, propertyType);
            }

            //转换值为null，则略过，表示无需进行设置值
            if(convertValue == null)
                continue;

            try{
                writeMethod.invoke(t, convertValue);
            } catch(IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return t;
    }

    /** 查找当前对象所在的类所继承的父类的第1个实现的泛型类型 */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> findExtendedFirstParameterizedType(Object obj) {
        Class<?> clazz = obj.getClass();

        return findExtendedFirstParameterizedType(clazz);
    }

    /** 查找当前类所继承的父类的第1个实现的泛型类型 */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> findExtendedFirstParameterizedType(Class clazz) {
        return (Class) ResolvableType.forClass(clazz).getSuperType().getGeneric(0).resolve();
    }

    /**
     * 查找当前类所在对象所实现的指定接口的的第一个泛型类型
     *
     * @param interfaceClass 相对应的接口类型 用于接口匹配
     */
    public static <T> Class<T> findImplementedFirstParameterizedType(Object obj, Class<?> interfaceClass) {
        Class<?> clazz = obj.getClass();
        return findImplementedFirstParameterizedType(clazz, interfaceClass);
    }

    /**
     * 查找当前类所实现的指定接口的的第一个泛型类型
     *
     * @param interfaceClass 相对应的接口类型 用于接口匹配
     */
    public static <T> Class<T> findImplementedFirstParameterizedType(Class<?> clazz, Class<?> interfaceClass) {
        @SuppressWarnings("unchecked")
        Class<T> result = (Class) ResolvableType.forClass(clazz).as(interfaceClass).getGeneric(0).resolve();
        if(result == null)
            throw new IllegalArgumentException(StringUtils.format("没有描述实现了特定接口的泛型信息:{},原始类:{}", interfaceClass, clazz));

        return result;
    }

    /**
     * 获取指定对象在工程类的真实类名(前提是此类为项目中的类)
     * <p>
     * 此类的作用在于避免在某些场景中需要获取原始类的反射信息时，由于对象被子类化(使用javassist或cglib)之后，相应的信息被丢失了
     * 这里即去除相应的生成信息
     */
    public static <T> Class<?> getRealClass(T obj) {
        Class sourceClass = obj.getClass();
        String shuyunPrefix = "com.shuyun";
        Class clazz = sourceClass;
        for(; clazz != Object.class; clazz = clazz.getSuperclass()) {
            String name = clazz.getName();

            //如果是cglib生成的，则忽略
            if(name.contains("CGLIB"))
                continue;

            if(name.startsWith(shuyunPrefix))
                return clazz;
        }

        return sourceClass;
    }
}