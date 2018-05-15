package com.silita.biaodaa.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectAnalysisUtil {

    /**
     * 解析面积
     * @param scope
     * @return
     */
    public static String analysisScope(String scope){
        //判断中间是否有总建筑面积
        int startIndex = -2;
        int endIndex = -2;
        if(scope.contains("总建筑面积")){
            startIndex = getIndex(scope,"总建筑面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = getUnitIndex(scope);
            scope = scope.substring("总建筑面积".length(),endIndex);
        }else if (scope.contains("建筑总面积")){
            startIndex = getIndex(scope,"建筑总面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = getUnitIndex(scope);
            scope = scope.substring("建筑总面积".length(),endIndex);
        }else if (scope.contains("总面积")){
            startIndex = getIndex(scope,"总面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = getUnitIndex(scope);
            scope = scope.substring("总面积".length(),endIndex);
        }else if (scope.contains("实际用地面积")){
            startIndex = getIndex(scope,"实际用地面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = getUnitIndex(scope);
            scope = scope.substring("实际用地面积".length(),endIndex);
        }else if (scope.contains("改造面积")){
            startIndex = getIndex(scope,"改造面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = getUnitIndex(scope);
            scope = scope.substring("改造面积".length(),endIndex);
        }else if(scope.contains("廉租住房")){
            startIndex = getIndex(scope,"廉租住房");
            scope = scope.substring(startIndex,scope.length());
            endIndex = getUnitIndex(scope);
            if(scope.contains("套")){
                scope = scope.substring(getIndex(scope,"套")+1,endIndex);
            }else{
                scope = scope.substring("廉租住房".length(),endIndex);
            }
        }else if (scope.contains("绿化面积")){
            startIndex = getIndex(scope,"绿化面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = getUnitIndex(scope);
            scope = scope.substring("绿化面积".length(),endIndex);
        }else if (scope.contains("支护为")){
            startIndex = getIndex(scope,"支护为");
            scope = scope.substring(startIndex,scope.length());
            endIndex = getUnitIndex(scope);
            scope = scope.substring("支护为".length(),endIndex);
        }else if (scope.contains("建筑面积")){
            startIndex = getIndex(scope,"建筑面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = getUnitIndex(scope);
            scope = scope.substring("建筑面积".length(),endIndex);
        }else if (scope.contains("扩建面积")){
            startIndex = getIndex(scope,"扩建面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = getUnitIndex(scope);
            scope = scope.substring("扩建面积".length(),endIndex);
        }else if (scope.contains("面积")){
            startIndex = getIndex(scope,"面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = getUnitIndex(scope);
            scope = scope.substring("面积".length(),endIndex);
        }else{
            scope = null;
        }
        return scope;
    }

    /**
     * 获取某个字符在字符串中的下标
     * @param str
     * @param chart
     * @return
     */
    public static int getIndex(String str,String chart){
        Matcher matcher = Pattern.compile(chart).matcher(str);
        int index = -2;
        if(matcher.find()){
            index = matcher.start();
        }
        return index;
    }

    /**
     * 获取单位所在字符串的下标
     * @param str
     * @return
     */
    public static int getUnitIndex(String str){
        int unitIndex = -2;
        if(str.contains("平方")){
            unitIndex = getIndex(str,"平方");
        }else if (str.contains("m2")){
            unitIndex = getIndex(str,"m2");
        }else if (str.contains("M2")){
            unitIndex = getIndex(str,"M2");
        }else if (str.contains("㎡")){
            unitIndex = getIndex(str,"㎡");
        }
        return unitIndex;
    }

    /**
     * 获取某个字符出现的次数和下标
     * @param scope
     * @param chart
     * @return
     */
    public static Map<String,Object> getIndexCount(String scope, String chart){
        Map<String,Object> map = new HashMap<>();
        List<Integer> indexCountList = new ArrayList<>();
        Matcher matcher = Pattern.compile(chart).matcher(scope);
        int count = 0;
        while (matcher.find()){
            count++;
            indexCountList.add(matcher.start());
        }
        map.put("count",count);
        map.put("indexCountList",indexCountList);
        return map;
    }

    /**
     * 截取日期
     * @param str
     * @return
     */
    public static String getStrDate(String str){
        String reg = "[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}";
        Matcher matcher = Pattern.compile(str).matcher(reg);
        if(matcher.find()){
            return matcher.group(0);
        }
        return str;
    }

    /**
     * 截取数字
     * @param str
     * @return
     */
    public static String getStrNumber(String str){
        String reg = "([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])";
        Matcher matcher = Pattern.compile(str).matcher(reg);
        while (matcher.find()){
            return matcher.group(0);
        }
        return str;
    }
}
