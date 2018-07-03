package com.silita.biaodaa.utils;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.util.regex.Pattern;

/** @author flym */
public class StringUtils {
    private static final Pattern colonPattern = Pattern.compile(":");
    private static final Pattern commaPattern = Pattern.compile(",");
    private static final Pattern dotPattern = Pattern.compile("\\.");
    private static final Pattern eqPattern = Pattern.compile("=");
    private static final Pattern andPattern = Pattern.compile("&");

    /** 根据并且符号进行分隔 */
    public static String[] splitByAnd(String source) {
        return andPattern.split(source, -1);
    }

    /** 根据等号进行分隔 */
    public static String[] splitByEq(String source) {
        return eqPattern.split(source, -1);
    }

    /** 根据点号进行分隔 */
    public static String[] splitByDot(String source) {
        return dotPattern.split(source, -1);
    }

    /** 根据冒号进行分隔 */
    public static String[] splitByColon(String source) {
        return colonPattern.split(source, -1);
    }

    /** 根据逗号进行分隔 */
    public static String[] splitByComma(String source) {
        return commaPattern.split(source, -1);
    }

    /** 移除两边的双引号或单引号 */
    public static String removeQuot(String str) {
        char[] quotChars = {'"', '\''};
        return removeBothChar(str, quotChars, quotChars);
    }

    /** 移除两边的中括号 */
    public static String removeMiddleBracket(String str) {
        return removeBothChar(str, '[', ']');
    }

    private static String removeBothChar(String str, char leftChar, char rightChar) {
        int start = 0;
        int end = str.length();

        char startChar = str.charAt(start);
        char endChar = str.charAt(end - 1);

        if(startChar == leftChar) {
            start = 1;
        }

        if(endChar == rightChar) {
            end = end - 1;
        }

        return str.substring(start, end);
    }

    private static String removeBothChar(String str, char[] leftChars, char[] rightChars) {
        int start = 0;
        int end = str.length();

        char startChar = str.charAt(start);
        char endChar = str.charAt(end - 1);

        for(char leftChar : leftChars) {
            if(startChar == leftChar) {
                start = 1;
                break;
            }
        }

        for(char rightChar : rightChars) {
            if(endChar == rightChar) {
                end = end - 1;
                break;
            }
        }

        return str.substring(start, end);
    }

    /** 对象转换为字符中，如果对象不存在，则转换为空串 */
    public static String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /** 查看指定的字符串有多少个指定的字符 */
    public static int count(String s, char c) {
        int i = 0;
        int start = 0;
        while((start = s.indexOf(c, start)) != -1) {
            i++;
            start += 1;
        }

        return i;
    }

    /**
     * 截取字符串至指定的countC个字符之后的字符串
     * 如a.b.c, ., 2 返回 c
     * 如果不能满足相应的字符条件，则返回null
     */
    public static String substring(String s, char c, int countC) {
        if(countC <= 0) {
            throw new IllegalArgumentException("计数值不能小于＝0:" + countC);
        }

        int i = 0;
        int start = 0;
        while((start = s.indexOf(c, start)) != -1) {
            i++;
            start += 1;
            if(i == countC) {
                return s.substring(start);
            }
        }

        return null;
    }

    /** 采用参数信息对原待处理字符串进行格式化,代替相应的占位符 */
    public static String format(String format, Object... args) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, args);
        return ft.getMessage();
    }

    /**
     * 判断字符串是否包含在一个集合中。
     *
     * @param params   要匹配的字符串。
     * @param patterns 匹配的字符串集合。
     * @return 包含返回true, 否则false。
     */
    public static boolean contains(String params, String... patterns) {
        for(String pattern : patterns) {
            if(params.equals(pattern)) {
                return true;
            }
        }
        return false;
    }

    /** 返回一个字符串的多倍值 */
    public static String multiply(String str, int factor) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < factor; i++) {
            builder.append(str);
        }

        return builder.toString();
    }
}
