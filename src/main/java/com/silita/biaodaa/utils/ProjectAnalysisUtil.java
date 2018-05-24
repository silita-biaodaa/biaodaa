package com.silita.biaodaa.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectAnalysisUtil {

    public static String regx = "[总建筑面积|建筑总面积|总面积|实际用地面积|改造面积|占地面积|建筑面积|扩建面积|总计|面积]?.*?(\\d\\d*?\\.?\\d*[万|亩]?\\s*(平方米|平方公里|M2|m2|㎡|万㎡|平米|万平|亩|平方))";

    /**
     * 解析面积
     * @param scope
     * @return
     */
    public static String analysisScope(String scope){
        //判断中间是否有总建筑面积
        int startIndex = -2;
//        int endIndex = -2;
        if(scope.contains("总建筑面积")){
            startIndex = getIndex(scope,"总建筑面积");
            scope = scope.substring(startIndex,scope.length());
            return scope;
        }else if (scope.contains("建筑总面积")){
            startIndex = getIndex(scope,"建筑总面积");
            scope = scope.substring(startIndex,scope.length());
            return scope;
        }else if (scope.contains("总面积")){
            startIndex = getIndex(scope,"总面积");
            scope = scope.substring(startIndex,scope.length());
            return scope;
        }else if (scope.contains("实际用地面积")){
            startIndex = getIndex(scope,"实际用地面积");
            scope = scope.substring(startIndex,scope.length());
            return scope;
        }else if (scope.contains("改造面积")){
            startIndex = getIndex(scope,"改造面积");
            scope = scope.substring(startIndex,scope.length());
            return scope;
        }else if (scope.contains("占地面积")){
            startIndex = getIndex(scope,"占地面积");
            scope = scope.substring(startIndex,scope.length());
            return scope;
        }else if (scope.contains("建筑面积")){
            if(charCount(scope,"面积") > 1){
                return null;
            }
            startIndex = getIndex(scope,"建筑面积");
            scope = scope.substring(startIndex,scope.length());
            scope = getScope(scope);
            return scope;
        }else if (scope.contains("扩建面积")){
            startIndex = getIndex(scope,"扩建面积");
            scope = scope.substring(startIndex,scope.length());
            return scope;
        }else if (scope.contains("总计")){
            startIndex = getIndex(scope,"总计");
            scope = scope.substring(startIndex,scope.length());
            return scope;
        }else if (scope.contains("面积")){
            if(charCount(scope,"面积") > 1){
                return null;
            }
            startIndex = getIndex(scope,"面积");
            scope = scope.substring(startIndex,scope.length());
            return scope;
        }else if (getUnitIndex(scope) > -2){
            return getScope(scope);
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
        }else if (str.contains("平米")){
            unitIndex = getIndex(str,"平米");
        }else if(str.contains("平方米")){
            unitIndex = getIndex(str,"平方米");
        }else if(str.contains("万㎡")){
            unitIndex = getIndex(str,"万㎡");
        }else if (str.contains("万平")){
            unitIndex = getIndex(str,"万平");
        }else if(str.contains("亩")){
            unitIndex = getIndex(str,"亩");
        }else if(str.contains("平方公里")){
            unitIndex = getIndex(str,"平方公里");
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
        String regex = "^\\d+(\\.\\d+)?";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        if(matcher.find()){
            return matcher.group(0);
        }
        return str;
    }

    public static int charCount(String scope,String str){
        int indexStart = 0;
        //获取查找字符串的长度，这里有个规律：第二次查找出字符串的起始位置等于 第一次ab字符串出现的位置+ab的长度
        int compareStrLength = str.length();
        int count = 0;
        while(true){
            int tm = scope.indexOf(str,indexStart);
            if( tm >= 0){
                count ++;
                //  没查找一次就从新计算下次开始查找的位置
                indexStart = tm+compareStrLength;
            }else{
                //直到没有匹配结果为止
                break;
            }
        }
        return count;
    }

    public static boolean isNumeric(String str){
        for (int i =  str.length(); --i>=0;) {
            if(Character.isDigit(str.charAt(i))){
                return true;
            }
        }
        return false;
    }

    public static String analysisData(String regex,String scope){
        String acreage;
        acreage = null;
        if(MyStringUtils.isNotNull(scope)){
            scope = analysisScope(scope);
            if(MyStringUtils.isNull(scope)){
                return null;
            }
            scope = scope.replaceAll(" ","");
            scope = scope.replaceAll("。",".");
            Matcher ma = Pattern.compile(regex).matcher(scope);
            if(ma.find()){
                acreage = ma.group(1);
            }
            if(StringUtils.isNotBlank(acreage) && isNumeric(acreage)){
                if(!acreage.equals("m2") && !acreage.equals("M2")){
                    return acreage;
                }
            }
        }
        return null;
    }

    public static String getNumber(String scope){
        String unit = "平方米";
        if(MyStringUtils.isNull(scope)){
            return null;
        }else if("M2".equals(scope) || "m2".equals(scope)){
            return null;
        }else if(scope.contains("万平") || scope.contains("万㎡")){
            unit = "万平方米";
        }else if(scope.contains("平方公里")){
            unit = "平方公里";
        }else if(scope.contains("亩")){
            unit = "亩";
        }
        return getStrNumber(scope)+unit;
    }

    public static String getScope(String scope){
        if(charCount(scope,"平方米") > 1){
            return null;
        }else if(charCount(scope,"M2") > 1){
            return null;
        }else if(charCount(scope,"m2") > 1){
            return null;
        }else if(charCount(scope,"万㎡") > 1){
            return null;
        }else if(charCount(scope,"平米") > 1){
            return null;
        }else if(charCount(scope,"万平") > 1){
            return null;
        }else if(charCount(scope,"亩") > 1){
            return null;
        }else if(charCount(scope,"平方") > 1){
            return null;
        }else if (charCount(scope,"㎡") > 1){
            return null;
        }else if(charCount(scope,"平方公里") > 1){
            return null;
        }else{
            return scope;
        }
    }
}
