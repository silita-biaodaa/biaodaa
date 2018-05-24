package com.silita.biaodaa.utils;




import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.silita.biaodaa.common.Constant.DEFAULT_STRING;

public class MyStringUtils {

	public static boolean isNotNull(Object str){
		if(str !=null ){
			if(str instanceof String ){
				if(!((String)str).trim().equals("")) {
					return true;
				}else{
					return false;
				}
			}else if(str instanceof List){
				if(((List)str).size()>0){
					return true;
				}else{
					return false;
				}
			}else{
				return true;
			}
		}else{
			return false;
		}
	}

	public static boolean isNull(String str){
		return !isNotNull(str);
	}

	public static boolean isNull(List str){
		return !isNotNull(str);
	}


	public static boolean isNull(Object str){
		boolean flag=true;
		if(str==null){
			return flag;
		}
		flag= !isNotNull(str);
		return flag;
	}

	public static List<String> StringSplit(String str, int num) {
		int length = str.length();
		List<String> listStr = new ArrayList<String>();
		int lineNum = length % num == 0 ? length / num : length / num + 1;
		String subStr = "";
		for(int i = 1; i <= lineNum; i++){
			if(i < lineNum){
				subStr = str.substring((i-1) * num, i * num);
			}else{
				subStr = str.substring((i-1) * num, length);
			}
			listStr.add(subStr);
		}
		return listStr;
	}

	public static boolean isNotDefaultStringAndNull(String var){
		if (var != null && !DEFAULT_STRING.equals(var)) {
			return true;
		}
		return false;
	}

	public static String deleteHtmlTag (String content) {
		content = content.replaceAll("\\s*",""); // 去除空格
		String regEx_html="<.+?>"; // HTML标签的正则表达式
		Pattern pattern = Pattern.compile(regEx_html);
		Matcher matcher = pattern.matcher(content);
		content = matcher.replaceAll("");
		content = content.replaceAll("&nbsp;","");
		content = content.replaceAll(" ","");
		return content;
	}

	/**
	 * 根据||分割参数
	 * @param str
	 * @return
	 */
	public static String[] splitParam(String str){
		if(isNotNull(str)){
			return  str.split("\\|\\|");
		}
		return null;
	}

}
