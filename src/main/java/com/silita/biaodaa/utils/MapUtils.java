package com.silita.biaodaa.utils;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 处理map的工具类
 *
 * @author flym
 */
public class MapUtils {
    /** 用于获取指定属性的name的字符串转换函数 */
    private static final Function<PropertyDescriptor, String> propertyFunction = PropertyDescriptor::getName;

    /**
     * 将集合对象按照指定的规则转换为map对象
     *
     * @param vCollection 待处理的集合对象
     * @param keyFunction 针对每个对象,待返回的map key值
     */
    public static <K, V> Map<K, V> asMap(Collection<V> vCollection, Function<V, K> keyFunction) {
        return asMap(vCollection, keyFunction, Functions.<V>identity());
    }

    /**
     * 将集合对象按照指定的规则转换为map对象
     *
     * @param vCollection 待处理的集合对象
     * @param mapSupplier 用于如何产生map的生成器
     * @param keyFunction 针对每个对象,待返回的map key值
     */
    public static <K, V> Map<K, V> asMap(Collection<V> vCollection, Supplier<Map<K, V>> mapSupplier, Function<V, K> keyFunction) {
        return asMap(vCollection, mapSupplier, keyFunction, Functions.<V>identity());
    }

    /**
     * 将集合对象按照指定的规则转换为map对象
     *
     * @param xCollection   待处理的集合对象
     * @param keyFunction   针对每个对象，待返回的map key值
     * @param valueFunction 针对每个对象，待返回的map value值
     */
    public static <K, V, X> Map<K, V> asMap(Collection<X> xCollection, Function<X, K> keyFunction, Function<X, V> valueFunction) {
        return asMap(xCollection, Suppliers.<Map<K, V>>ofInstance(Maps.<K, V>newHashMap()), keyFunction, valueFunction);
    }

    /**
     * 将集合对象按照指定的规则转换为map对象
     *
     * @param xCollection   待处理的集合对象
     * @param mapSupplier   用于如何产生map的生成器
     * @param keyFunction   针对每个对象，待返回的map key值
     * @param valueFunction 针对每个对象，待返回的map value值
     */
    public static <K, V, X> Map<K, V> asMap(Collection<X> xCollection, Supplier<Map<K, V>> mapSupplier, Function<X, K> keyFunction, Function<X, V> valueFunction) {
        Map<K, V> map = mapSupplier.get();
        for(X x : xCollection) {
            K k = keyFunction.apply(x);
            V v = valueFunction.apply(x);
            map.put(k, v);
        }
        return map;
    }

    /** 将指定对象转换为map */
    public static <T> Map<String, ?> asMap(T instance) {
        return asMap(instance, propertyFunction, Functions.identity());
    }

    /** 将指定对象转换为map */
    public static <T, V> Map<String, V> asMap(T instance, Function<Object, V> valueFunction) {
        return asMap(instance, propertyFunction, valueFunction);
    }

    /** 将指定对象转换为map */
    public static <T, K, V> Map<K, V> asMap(T instance, Function<PropertyDescriptor, K> keyFunction, Function<Object, V> valueFunction) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) instance.getClass();
        Map<K, V> map = Maps.newHashMap();

        PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(clazz);
        try{
            for(PropertyDescriptor descriptor : descriptors) {
                //忽略class
                if(Objects.equals("class", descriptor.getName()))
                    continue;

                //目标没有可读方法，则略过
                Method readMethod = descriptor.getReadMethod();
                if(readMethod == null)
                    continue;

                //源值为null，略过
                Object value = readMethod.invoke(instance);
                if(value == null)
                    continue;

                K k = keyFunction.apply(descriptor);
                V v = valueFunction.apply(value);

                map.put(k, v);
            }
        } catch(IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return map;
    }

    /**
     * 传入一个集合，返回按Function指定的内容进行GroupBy后的Map对象
     *
     * @param collection 集合类
     * @param function   分组函数
     * @param <T>        Map_value类型
     * @param <K>        Map_Key类型
     * @return 传入列表，通过Groupby后，以Groupby的字段为K，以相同Key的对象封装成一个List做为V，返回Map&lt;K,V>
     */
    public static <T, K> Map<K, List<T>> asListMap(Collection<T> collection, Function<T, K> function) {
        return asListMap(collection, function, Functions.<T>identity());
    }

    /** 将集合转换为group之后的listMap 将提供指定的map实现 */
    public static <T, K> Map<K, List<T>> asListMap(Collection<T> collection, Function<T, K> keyFunction, Supplier<Map<K, List<T>>> mapSupplier) {
        return asListMap(collection, keyFunction, Functions.<T>identity(), mapSupplier);
    }

    /** 将集合转换为group之后的listMap */
    public static <T, K, V> Map<K, List<V>> asListMap(Collection<T> collection, Function<T, K> keyFunction, Function<T, V> valueFunction) {
        return asListMap(collection, keyFunction, valueFunction, Suppliers.<Map<K, List<V>>>ofInstance(Maps.<K, List<V>>newHashMap()));
    }

    /** 将集合转换为group之后的listMap */
    public static <T, K, V> Map<K, List<V>> asListMap(Collection<T> collection, Function<T, K> keyFunction, Function<T, V> valueFunction, Supplier<Map<K, List<V>>> mapSupplier) {
        Map<K, List<V>> map = mapSupplier.get();
        for(T input : collection) {
            K key = keyFunction.apply(input);
            V value = valueFunction.apply(input);
            List<V> list = map.get(key);
            if(list != null) {
                list.add(value);
            } else {
                @SuppressWarnings("unchecked")
                List<V> valueList = Lists.newArrayList(value);
                map.put(key, valueList);
            }
        }
        return map;
    }
}