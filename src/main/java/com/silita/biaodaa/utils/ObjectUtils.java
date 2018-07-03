package com.silita.biaodaa.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by dh on 2018/4/12.
 */
public class ObjectUtils {
    private static Log logger = LogFactory.getLog(ObjectUtils.class);


    /**
     * 根据前缀+Map参数对象，生成缓存KEY
     * @param prefix
     * @param paramMap
     * @return
     */
    public static String buildCacheKey(String prefix, Map paramMap){
        int hash = buildMapParamHash(paramMap);
        return prefix+hash;
    }

    /**
     * 根据MAP对象的属性值计算出唯一的hash值。
     * @param paramMap
     * @return 可返回负数
     */
    public static int buildMapParamHash(Map paramMap){
        int result =-1;
        Set paramKeys = paramMap.keySet();
        Iterator sIterator = paramKeys.iterator();
        String[] s =new String[paramKeys.size()];
        int i=0;
        while(sIterator.hasNext()){
            String key = (String)sIterator.next();
            Object value = paramMap.get(key);
            s[i]=key+value;
            i++;
        }
        if(s !=null && s.length>0){
            Arrays.sort(s);
            return JsonUtils.obj2JsonString(s).hashCode();
        }
        return result;
    }

    /**
     * 自定义vo类，生成hash值
     * @param paramMap
     * @return
     */
    public static int buildParamHash(Object paramMap){
        int result =-1;
        String paramAttrs= buildObjectAttr(paramMap);
        if(paramAttrs.length()>0){
            paramAttrs = sortString(paramAttrs);
            result =paramAttrs.hashCode();
        }
        return result;
    }

    private static String buildObjectAttr(Object parameterObj){
        StringBuffer sb = new StringBuffer("");
        try {
            Class clazz = parameterObj.getClass();
            Method[] methods = clazz.getMethods();

            for (Method method : methods) {
                String methodName = method.getName();
//				System.out.println("方法名称:" + methodName);
                if (methodName.indexOf("get") == 0 && !methodName.equals("getClass")) {
                    Class<?> returnType = method.getReturnType();
                    if(returnType.isPrimitive()
                            || returnType.isAssignableFrom(String.class)
                            || returnType.isAssignableFrom(Integer.class)
                            || returnType.isAssignableFrom(Long.class)
                            || returnType.isAssignableFrom(Double.class)
                            || returnType.isAssignableFrom(Float.class)
                            || returnType.isAssignableFrom(Date.class)
                            || returnType.isAssignableFrom(Timestamp.class)){
                        Object value =method.invoke(parameterObj);
                        sb.append(value);
                    }else{
                        Object value =method.invoke(parameterObj);
                        if(value!=null){
                            sb.append(buildObjectAttr(value));
//							System.out.println(methodName);
                        }

                    }
                }else{
//					if((methodName.indexOf("get") == 0)) {
//						System.out.println("排除的get方法：" + methodName);
//					}
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return sb.toString();
    }

    /**
     * 对字符串进行排序
     * @param str
     * @return
     */
    public static String sortString(String str){
        //利用toCharArray可将字符串转换为char型的数组
        char[] s1 = str.toCharArray();
        for(int i=0;i<s1.length;i++){
            for(int j=0;j<i;j++){
                if(s1[i]<s1[j]){
                    char temp = s1[i];
                    s1[i] = s1[j];
                    s1[j] = temp;
                }
            }
        }
        //再次将字符数组转换为字符串，也可以直接利用String.valueOf(s1)转换
        String st = new String(s1);
        return st;
    }

    /**
     * 判断一个对象是否为null或empty对象
     * empty对象包括空字符串(包括空格串)，空数组,字集合等
     */
    public static <T> boolean isEmpty(T t) {
        //null对象
        if(t == null)
            return true;
        //序列串
        if(t instanceof CharSequence) {
            CharSequence cs = (CharSequence) t;
            for(int i = 0; i < cs.length(); i++) {
                if(!Character.isWhitespace(cs.charAt(i)))
                    return false;
            }
            return true;
        }
        //数组
        if(t.getClass().isArray())
            return Array.getLength(t) == 0;
        //集合
        if(t instanceof Collection)
            return ((Collection) t).isEmpty();
        //map
        if(t instanceof Map)
            return ((Map) t).isEmpty();
        //默认为false,即非null
        return false;
    }

    /**
     * 判断一个对象在业务逻辑上是否是null
     * 包括 isNullOrEmpty判断，以及数字0, 空值数组，空值集合等
     */
    public static <T> boolean isEmptyOrZero(T t) {
        if(isEmpty(t))
            return true;

        //数字
        if(t instanceof Number) {
            return Double.doubleToRawLongBits(((Number) t).doubleValue()) == 0;
        }

        //数组
        if(t.getClass().isArray()) {
            for(int i = 0, j = Array.getLength(t); i < j; i++) {
                Object object = Array.get(t, i);
                if(!isEmptyOrZero(object))
                    return false;
            }

            return true;
        }

        //空值集合
        if(t instanceof Collection) {
            for(Object obj : (Collection) t) {
                if(!isEmptyOrZero(obj))
                    return false;
            }

            return true;
        }

        return false;
    }

}
