package com.silita.biaodaa.utils;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dh on 2018/11/2.
 */
public class RegexUtils {
    private static Logger logger = Logger.getLogger(RegexUtils.class);

    /**
     * 判断字符是否有匹配的值。
     *
     * @param s
     * @param regex
     * @return
     */
    public static boolean matchExists(String s, String regex) {
        boolean isExists = false;
        try {
            if (s != null) {
                Pattern ptn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher mt = ptn.matcher(s);
                isExists = mt.find();
//            logger.debug("find:" + isExists);
                if (isExists) {
                    logger.debug("group:" + mt.group());
                }
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
        return isExists;
    }

    /**
     * 遍历字符数组，返回匹配表达式的集合
     *
     * @param arr
     * @param regex
     * @return
     */
    public static List<String> matchExists(String[] arr, String regex) {
        List<String> matchList = new ArrayList<String>();
        if (arr != null) {
            for (String s : arr) {
                if (matchExists(s, regex)) {
                    matchList.add(s);
                }
            }
        }
        return matchList;
    }

    /**
     * 提取匹配表达式的值
     *
     * @param s
     * @param regex
     * @return
     */
    public static String matchValue(String s, String regex) {
        String mStr = null;
        try {
            if (s != null) {
                Pattern ptn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher mt = ptn.matcher(s);
                if (mt.find()) {
                    mStr = mt.group();
                    logger.debug("group:" + mt.group());
                }
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
        return mStr;
    }

    public static String insertMatchValuePos(String s, String regex, String posfix) {
        String mStr = null;
        try {
            if (s != null) {
                StringBuilder sb = new StringBuilder(s);
                Pattern ptn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher mt = ptn.matcher(s);
                while (mt.find()) {
                    mStr = mt.group();
                    int sIdx = sb.indexOf(mStr);
                    int eIdx = sIdx + mStr.length();
                    //匹配字符后面没有posfix时，才进行插入补充
                    if ((eIdx + 1) < s.length()
                            && s.substring(eIdx, eIdx + 1).indexOf(posfix) == -1) {
                        sb.insert(eIdx, posfix);
                    } else if (eIdx + 1 > s.length()) {
                        sb.insert(eIdx, posfix);
                    }
                }
                if (s.length() < sb.length()) {
                    s = sb.toString();
                }
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
        return s;
    }

    /**
     * 过滤公司名
     * @param comName
     * @return
     */
    public static String setComName(String comName) {
        if (MyStringUtils.isNull(comName)) {
            return comName;
        }
        comName = comName.replace("湖南", "");
        comName = comName.replace("长沙", "");
        comName = comName.replace("株洲", "");
        comName = comName.replace("湘潭", "");
        comName = comName.replace("衡阳", "");
        comName = comName.replace("邵阳", "");
        comName = comName.replace("岳阳", "");
        comName = comName.replace("常德", "");
        comName = comName.replace("张家界", "");
        comName = comName.replace("益阳", "");
        comName = comName.replace("娄底", "");
        comName = comName.replace("郴州", "");
        comName = comName.replace("永州", "");
        comName = comName.replace("怀化", "");
        comName = comName.replace("湘西", "");

        comName = comName.replace("集团", "");
        comName = comName.replace("有限", "");
        comName = comName.replace("责任", "");
        comName = comName.replace("公司", "");
        comName = comName.replace("工程", "");
        comName = comName.replace("建设", "");
        comName = comName.replace("建筑", "");
        comName = comName.replace("）", "");
        comName = comName.replace("（", "");

        comName = comName.replace("土整", "");
        comName = comName.replace("农田", "");
        comName = comName.replace("农开", "");
        comName = comName.replace("农改", "");
        comName = comName.replace("水利", "");
        comName = comName.replace("国土", "");
        comName = comName.replace("绿化", "");
        comName = comName.replace("园林", "");
        comName = comName.replace("人防", "");
        comName = comName.replace("地灾", "");
        comName = comName.replace("市政", "");
        comName = comName.replace("房建", "");
        comName = comName.replace("学校", "");
        comName = comName.replace("村部", "");
        comName = comName.replace("公路", "");
        comName = comName.replace("电力", "");
        comName = comName.replace("水库", "");
        comName = comName.replace("公租房", "");
        comName = comName.replace("配电", "");
        comName = comName.replace("装修", "");
        comName = comName.replace("装饰", "");

        comName = comName.replace("监理", "");
        comName = comName.replace("项目", "");
        comName = comName.replace("市","");
        comName  = comName.replace("省","");
        return comName;
    }
}
