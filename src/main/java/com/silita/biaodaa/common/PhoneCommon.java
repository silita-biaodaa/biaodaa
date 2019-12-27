package com.silita.biaodaa.common;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneCommon {

    public static String phones(String phone,Integer isVip) {

        Map<String, Object> param = new HashMap<>();

        Pattern patternPhone = Pattern.compile("((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[0-9])|(18[0,5-9]))\\d{8}");
        Pattern patternFixed = Pattern.compile("(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcherPhone = patternPhone.matcher(phone);
        Matcher matcherFixed = patternFixed.matcher(phone);
        String a = "";
        String b = "";


        //查找字符串中是否有符合的子字符串
        while (matcherPhone.find()) {
            if (isVip != null && isVip == 1) {
                a = matcherPhone.group() + ";" + a;
            } else {
                String phoneGroup = matcherPhone.group();
                String s = phoneGroup.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                a = s + ";" + a;
            }
        }
        //查找字符串中是否有符合的子字符串
        while (matcherFixed.find()) {
            if (isVip != null && isVip == 1) {
                b = matcherFixed.group() + ";" + b;
            } else {
                String fixedGroup = matcherFixed.group();
                String[] split = fixedGroup.split("");
                if (split[4].equals("-") && (split.length == 12 || split.length == 13)) {
                    String substring = fixedGroup.substring(0, 4);
                    String substring1 = fixedGroup.substring(5);
                    String r = substring + substring1;
                    String fixed = r.replaceAll("(\\d{4})\\d{4}(\\d{3,4})", "$1****$2");
                    String substring2 = fixed.substring(0, 4);
                    String substring3 = fixed.substring(4);
                    String fix = substring2 + "-" + substring3;
                    b = fix + ";" + b;
                } else if (split[3].equals("-") && split.length == 12) {
                    String substring = fixedGroup.substring(0, 3);
                    String substring1 = fixedGroup.substring(4);
                    String r = substring + substring1;
                    String fixed = r.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                    String substring2 = fixed.substring(0, 3);
                    String substring3 = fixed.substring(3);
                    String fix = substring2 + "-" + substring3;
                    b = fix + ";" + b;
                }
            }
        }

        return a + b;
    }



}
