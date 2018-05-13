package com.silita.biaodaa.utils;


public class RouteUtils {
    public static String HUNAN_SOURCE = "hunan";

    public static String routeTableName(String tbName, String source) {
        if(MyStringUtils.isNull(source) || source.equals(HUNAN_SOURCE)){
            return tbName;
        }else{
            return tbName+"_"+source;
        }
    }
}
