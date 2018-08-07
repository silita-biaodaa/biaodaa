package com.silita.biaodaa.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类 - 公用
 */

public class CommonUtil {

	private static Logger logger = Logger.getLogger(CommonUtil.class);

	public static final String WEB_APP_ROOT_KEY = "anyvape.webAppRoot";// WebRoot路径KEY
	public static final String PATH_PREPARED_STATEMENT_UUID = "\\{uuid\\}";// UUID路径占位符
	public static final String PATH_PREPARED_STATEMENT_DATE = "\\{date(\\(\\w+\\))?\\}";// 日期路径占位符


	/**
	 * 把中文字符进行分割
	 * @param s
	 * @return
	 */
	public static List<Character> splitChinese(String s){
		List<Character> list = new ArrayList<Character>();
		Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]+");
		Matcher m = p.matcher( s );
		while ( m.find() ) {
			String subStr = m.group();
			for(int i= 0 ;i<subStr.length();i++){
				char temp = subStr.charAt(i);
				list.add(temp);
//				System.out.println(temp);
			}
		}
		return list;
	}

	/**
	 * 获取WebRoot路径
	 * 
	 * @return WebRoot路径
	 */
	public static String getWebRootPath() {
		return System.getProperty(WEB_APP_ROOT_KEY);
	}

	/**
	 * 随机获取UUID字符串(无中划线)
	 * 
	 * @return UUID字符串
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		return uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23) + uuid.substring(24);
	}
	
	/**
	 * 获取实际路径
	 * 
	 * @param path
	 *            路径
	 */
	public static String getPreparedStatementPath(String path) {
		if (StringUtils.isEmpty(path)) {
			return null;
		}
		StringBuffer uuidStringBuffer = new StringBuffer();
		Matcher uuidMatcher = Pattern.compile(PATH_PREPARED_STATEMENT_UUID).matcher(path);
		while(uuidMatcher.find()) {
			uuidMatcher.appendReplacement(uuidStringBuffer, CommonUtil.getUUID());
		}
		uuidMatcher.appendTail(uuidStringBuffer);
		
		StringBuffer dateStringBuffer = new StringBuffer();
		Matcher dateMatcher = Pattern.compile(PATH_PREPARED_STATEMENT_DATE).matcher(uuidStringBuffer.toString());
		while(dateMatcher.find()) {
			String dateFormate = "yyyyMM";
			Matcher dateFormatMatcher = Pattern.compile("\\(\\w+\\)").matcher(dateMatcher.group());
			if (dateFormatMatcher.find()) {
				String dateFormatMatcherGroup = dateFormatMatcher.group();
				dateFormate = dateFormatMatcherGroup.substring(1, dateFormatMatcherGroup.length() - 1);
			}
			dateMatcher.appendReplacement(dateStringBuffer, new SimpleDateFormat(dateFormate).format(new Date()));
		}
		dateMatcher.appendTail(dateStringBuffer);
		
		return dateStringBuffer.toString();
	}

/**
 * 判断是否是正整数，并且不包括零
 * 
 * @param str
 * @return
 */
public static boolean isPositiveInteger(String str) {
	return Pattern.compile("^[1-9]+[0-9]*$").matcher(str).find();
}
/**
 * 判断是否是正整数，并且不包括零
 * 
 * @param str
 * @return
 */
public static boolean isPositiveInteger(int str) {
	return Pattern.compile("^[1-9]+[0-9]*$").matcher(String.valueOf(str)).find();
}
/**
 * 判断是否是正整数，并且不包括零
 * 
 * @param str
 * @return
 */
public static boolean isPositiveInteger(long str) {
	return Pattern.compile("^[1-9]+[0-9]*$").matcher(String.valueOf(str)).find();
}

private abstract static class WordTokenizer {
	protected static final char UNDERSCORE = '_';

	/**
	 * Parse sentence。
	 */
	public String parse(String str) {
		if (StringUtils.isEmpty(str)) {
			return str;
		}

		int length = str.length();
		StringBuffer buffer = new StringBuffer(length);

		for (int index = 0; index < length; index++) {
			char ch = str.charAt(index);

			// 忽略空白。
			if (Character.isWhitespace(ch)) {
				continue;
			}

			// 大写字母开始：UpperCaseWord或是TitleCaseWord。
			if (Character.isUpperCase(ch)) {
				int wordIndex = index + 1;

				while (wordIndex < length) {
					char wordChar = str.charAt(wordIndex);

					if (Character.isUpperCase(wordChar)) {
						wordIndex++;
					} else if (Character.isLowerCase(wordChar)) {
						wordIndex--;
						break;
					} else {
						break;
					}
				}

				// 1. wordIndex == length，说明最后一个字母为大写，以upperCaseWord处理之。
				// 2. wordIndex == index，说明index处为一个titleCaseWord。
				// 3. wordIndex > index，说明index到wordIndex -
				// 1处全部是大写，以upperCaseWord处理。
				if ((wordIndex == length) || (wordIndex > index)) {
					index = parseUpperCaseWord(buffer, str, index,
							wordIndex);
				} else {
					index = parseTitleCaseWord(buffer, str, index);
				}

				continue;
			}

			// 小写字母开始：LowerCaseWord。
			if (Character.isLowerCase(ch)) {
				index = parseLowerCaseWord(buffer, str, index);
				continue;
			}

			// 数字开始：DigitWord。
			if (Character.isDigit(ch)) {
				index = parseDigitWord(buffer, str, index);
				continue;
			}

			// 非字母数字开始：Delimiter。
			inDelimiter(buffer, ch);
		}

		return buffer.toString();
	}

	private int parseUpperCaseWord(StringBuffer buffer, String str,
			int index, int length) {
		char ch = str.charAt(index++);

		// 首字母，必然存在且为大写。
		if (buffer.length() == 0) {
			startSentence(buffer, ch);
		} else {
			startWord(buffer, ch);
		}

		// 后续字母，必为小写。
		for (; index < length; index++) {
			ch = str.charAt(index);
			inWord(buffer, ch);
		}

		return index - 1;
	}

	private int parseLowerCaseWord(StringBuffer buffer, String str,
			int index) {
		char ch = str.charAt(index++);

		// 首字母，必然存在且为小写。
		if (buffer.length() == 0) {
			startSentence(buffer, ch);
		} else {
			startWord(buffer, ch);
		}

		// 后续字母，必为小写。
		int length = str.length();

		for (; index < length; index++) {
			ch = str.charAt(index);

			if (Character.isLowerCase(ch)) {
				inWord(buffer, ch);
			} else {
				break;
			}
		}

		return index - 1;
	}

	private int parseTitleCaseWord(StringBuffer buffer, String str,
			int index) {
		char ch = str.charAt(index++);

		// 首字母，必然存在且为大写。
		if (buffer.length() == 0) {
			startSentence(buffer, ch);
		} else {
			startWord(buffer, ch);
		}

		// 后续字母，必为小写。
		int length = str.length();

		for (; index < length; index++) {
			ch = str.charAt(index);

			if (Character.isLowerCase(ch)) {
				inWord(buffer, ch);
			} else {
				break;
			}
		}

		return index - 1;
	}

	private int parseDigitWord(StringBuffer buffer, String str, int index) {
		char ch = str.charAt(index++);

		// 首字符，必然存在且为数字。
		if (buffer.length() == 0) {
			startDigitSentence(buffer, ch);
		} else {
			startDigitWord(buffer, ch);
		}

		// 后续字符，必为数字。
		int length = str.length();

		for (; index < length; index++) {
			ch = str.charAt(index);

			if (Character.isDigit(ch)) {
				inDigitWord(buffer, ch);
			} else {
				break;
			}
		}

		return index - 1;
	}

	protected boolean isDelimiter(char ch) {
		return !Character.isUpperCase(ch) && !Character.isLowerCase(ch)
				&& !Character.isDigit(ch);
	}

	protected abstract void startSentence(StringBuffer buffer, char ch);

	protected abstract void startWord(StringBuffer buffer, char ch);

	protected abstract void inWord(StringBuffer buffer, char ch);

	protected abstract void startDigitSentence(StringBuffer buffer, char ch);

	protected abstract void startDigitWord(StringBuffer buffer, char ch);

	protected abstract void inDigitWord(StringBuffer buffer, char ch);

	protected abstract void inDelimiter(StringBuffer buffer, char ch);
}

private static final WordTokenizer CAMEL_CASE_TOKENIZER = new WordTokenizer() {

	@Override
	protected void startSentence(StringBuffer buffer, char ch) {
		buffer.append(Character.toLowerCase(ch));
	}

	@Override
	protected void startWord(StringBuffer buffer, char ch) {
		if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
			buffer.append(Character.toUpperCase(ch));
		} else {
			buffer.append(Character.toLowerCase(ch));
		}
	}

	@Override
	protected void inWord(StringBuffer buffer, char ch) {
		buffer.append(Character.toLowerCase(ch));
	}

	@Override
	protected void startDigitSentence(StringBuffer buffer, char ch) {
		buffer.append(ch);
	}

	@Override
	protected void startDigitWord(StringBuffer buffer, char ch) {
		buffer.append(ch);
	}

	@Override
	protected void inDigitWord(StringBuffer buffer, char ch) {
		buffer.append(ch);
	}

	@Override
	protected void inDelimiter(StringBuffer buffer, char ch) {
		if (ch != UNDERSCORE) {
			buffer.append(ch);
		}
	}
};

private static final WordTokenizer UPPER_CASE_WITH_UNDERSCORES_TOKENIZER = new WordTokenizer() {

	@Override
	protected void startSentence(StringBuffer buffer, char ch) {
		buffer.append(Character.toUpperCase(ch));
	}

	@Override
	protected void startWord(StringBuffer buffer, char ch) {
		if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
			buffer.append(UNDERSCORE);
		}

		buffer.append(Character.toUpperCase(ch));
	}

	@Override
	protected void inWord(StringBuffer buffer, char ch) {
		buffer.append(Character.toUpperCase(ch));
	}

	@Override
	protected void startDigitSentence(StringBuffer buffer, char ch) {
		buffer.append(ch);
	}

	@Override
	protected void startDigitWord(StringBuffer buffer, char ch) {
		if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
			buffer.append(UNDERSCORE);
		}

		buffer.append(ch);
	}

	@Override
	protected void inDigitWord(StringBuffer buffer, char ch) {
		buffer.append(ch);
	}

	@Override
	protected void inDelimiter(StringBuffer buffer, char ch) {
		buffer.append(ch);
	}
	};

	public static String toCamelCase(String str) {
		if (str == null)
			return null;
		return CAMEL_CASE_TOKENIZER.parse(str);
	}

	public static String toUpperCaseWithUnderscores(String str) {
		if (str == null)
			return null;
		return UPPER_CASE_WITH_UNDERSCORES_TOKENIZER.parse(str);
	}

	public static String toLowerCaseWithUnderscores(String str) {
		if (str == null)
			return null;
		return toUpperCaseWithUnderscores(str).toLowerCase();
	}

	public static boolean matchesWildcard(String text, String pattern) {
		text += '\0';
		pattern += '\0';

		int N = pattern.length();

		boolean[] states = new boolean[N + 1];
		boolean[] old = new boolean[N + 1];
		old[0] = true;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			states = new boolean[N + 1];
			for (int j = 0; j < N; j++) {
				char p = pattern.charAt(j);

				if (old[j] && (p == '*'))
					old[j + 1] = true;

				if (old[j] && (p == c))
					states[j + 1] = true;
				if (old[j] && (p == '?'))
					states[j + 1] = true;
				if (old[j] && (p == '*'))
					states[j] = true;
				if (old[j] && (p == '*'))
					states[j + 1] = true;
			}
			old = states;
		}
		return states[N];
	}

	public static String trimTail(String input, String tail) {
		if (input == null || tail == null || !input.endsWith(tail))
			return input;
		return input.substring(0, input.length() - tail.length());
	}

	public static String trimTailSlash(String input) {
		return trimTail(input, "/");
	}

	public static String compressRepeat(String input, String repeat) {
		if (input == null || repeat == null)
			return input;
		String s = repeat + repeat;
		while (input.contains(s))
			input = input.replace(s, repeat);
		return input;
	}

	public static String compressRepeatSlash(String input) {
		return compressRepeat(input, "/");
	}

	public static boolean isNumericOnly(String cs) {
		if (cs == null || cs.trim().length() == 0) {
			return false;
		}
		int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static String decodeUrl(String url) {
		try {
			if (isUtf8Url(url)) {
				return URLDecoder.decode(url, "UTF-8");
			} else {
				return URLDecoder.decode(url, "GBK");
			}
		} catch (UnsupportedEncodingException e) {
			return url;
		}
	}

	private static boolean utf8codeCheck(String urlCode) {
		StringBuilder sign = new StringBuilder();
		if (urlCode.startsWith("%e"))
			for (int p = 0; p != -1;) {
				p = urlCode.indexOf("%", p);
				if (p != -1)
					p++;
				sign.append(p);
			}
		return sign.toString().equals("147-1");
	}

	private static boolean isUtf8Url(String urlCode) {
		urlCode = urlCode.toLowerCase();
		int p = urlCode.indexOf("%");
		if (p != -1 && urlCode.length() - p > 9) {
			urlCode = urlCode.substring(p, p + 9);
		}
		return utf8codeCheck(urlCode);
	}

	public static String trimLocale(String s) {
		if (s == null)
			return null;
		if (s.indexOf('_') < 0)
			return s;
		for (Locale locale : Locale.getAvailableLocales()) {
			String suffix = "_" + locale.getLanguage();
			if (s.endsWith(suffix))
				return s.substring(0, s.length() - suffix.length());
			suffix += "_" + locale.getCountry();
			if (s.endsWith(suffix))
				return s.substring(0, s.length() - suffix.length());
			suffix += "_" + locale.getVariant();
			if (s.endsWith(suffix))
				return s.substring(0, s.length() - suffix.length());
		}
		return s;
	}

	public static String htmlspecialchars(String str) {
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("\"", "&quot;");
		return str;
	}

	/**
	 * 企业资质id拼接
	 * @param uuid
	 * @param rank
	 * @return
	 */
	public static String certSpellUuid(String uuid,String rank) {
		String str="";
		//特级
		if("0".equals(rank)){
			str +=uuid+"/0";
		}
		//一级
		else if("1".equals(rank)){
			str +=uuid+"/1";
		}
		//二级
		else if("2".equals(rank)){
			str +=uuid+"/2";
		}
		//三级
		else if("3".equals(rank)){
			str +=uuid+"/3";
		}
		//一级及以上
		else if("u1".equals(rank)){
			str +=uuid+"/0"+","+uuid+"/1";
		}
		//二级及以上
		else if("u2".equals(rank)){
			str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2";
		}
		//三级及以上
		else if("u3".equals(rank)){
			str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2"+","+uuid+"/3";
		}
		//特级及以下
		else if("d0".equals(rank)){
			str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2"+","+uuid+"/3";
		}
		//一级及以下
		else if("d1".equals(rank)){
			str +=uuid+"/1"+","+uuid+"/2"+","+uuid+"/3";
		}
		//二级及以下
		else if("d2".equals(rank)){
			str +=uuid+"/2"+","+uuid+"/3";
		}
		//一级及以上
		else if("11".equals(rank)){
			str +=uuid+"/0"+","+uuid+"/1";
		}
		//二级及以上
		else if("21".equals(rank)){
			str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2";
		}
		//三级及以上
		else if("31".equals(rank)){
			str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2"+","+uuid+"/3";
		}
		//无等级
		else if("no".equals(rank)){
			str +=uuid+"/";
		}

		//甲级
		else if("-1".equals(rank)){
			str +=uuid+"/-1";
		}
		//乙级
		else if("-2".equals(rank)){
			str +=uuid+"/-2";
		}
		//丙级
		else if("-3".equals(rank)){
			str +=uuid+"/-3";
		}
		//乙级及以上
		else if("-21".equals(rank)){
			str +=uuid+"/-1"+","+uuid+"/-2";
		}
		//丙级及以上
		else if("-31".equals(rank)){
			str +=uuid+"/-1"+","+uuid+"/-2"+","+uuid+"/-3";
		}
		//乙级及以上
		else if("u-2".equals(rank)){
			str +=uuid+"/-1"+","+uuid+"/-2";
		}
		//丙级及以上
		else if("u-3".equals(rank)){
			str +=uuid+"/-1"+","+uuid+"/-2"+","+uuid+"/-3";
		}
		return str;
	}

	/**
	 * 资质id拼接
	 * @param uuid
	 * @param rank
	 * @return
	 */
	public static String spellUuid(String uuid,String rank) {
		String str="";
		//特级
		if("0".equals(rank)){
			str +=uuid+"/0";
		}
		//一级
		else if("1".equals(rank)){
			str +=uuid+"/1";
		}
		//二级
		else if("2".equals(rank)){
			str +=uuid+"/2";
		}
		//三级
		else if("3".equals(rank)){
			str +=uuid+"/3";
		}
		//一级及以上
		else if("u1".equals(rank)){
			str +=uuid+"/0"+"','"+uuid+"/1"+"','";
			str +=uuid+"/0"+","+uuid+"/1";
		}
		//二级及以上
		else if("u2".equals(rank)){
			str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2"+"','";
			str +=uuid+"/0"+","+uuid+"/1"+"','";
			str +=uuid+"/0"+"','"+uuid+"/1"+"','"+uuid+"/2";
		}
		//三级及以上
		else if("u3".equals(rank)){
			str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2"+","+uuid+"/3"+"','";
			str +=uuid+"/0"+","+uuid+"/1"+"','";
			str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2"+"','";
			str +=uuid+"/0"+"','"+uuid+"/1"+"','"+uuid+"/2"+"','"+uuid+"/3";
		}
		//特级及以下
		else if("d0".equals(rank)){
			str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2"+","+uuid+"/3";
		}
		//一级及以下
		else if("d1".equals(rank)){
			str +=uuid+"/1"+","+uuid+"/2"+","+uuid+"/3";
		}
		//二级及以下
		else if("d2".equals(rank)){
			str +=uuid+"/2"+","+uuid+"/3";
		}
		//一级及以上
		else if("11".equals(rank)){
			str +=uuid+"/0"+"','"+uuid+"/1"+"','";
			str +=uuid+"/0"+","+uuid+"/1";
		}
		//二级及以上
		else if("21".equals(rank)){
			str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2"+"','";
			str +=uuid+"/0"+","+uuid+"/1"+"','";
			str +=uuid+"/0"+"','"+uuid+"/1"+"','"+uuid+"/2";
		}
		//三级及以上
		else if("31".equals(rank)){
			str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2"+","+uuid+"/3"+"','";
			str +=uuid+"/0"+","+uuid+"/1"+"','";
			str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2"+"','";
			str +=uuid+"/0"+"','"+uuid+"/1"+"','"+uuid+"/2"+"','"+uuid+"/3";
		}
		//无等级
		else if("no".equals(rank)){
			str +=uuid+"/";
		}

		//甲级
		else if("-1".equals(rank)){
			str +=uuid+"/-1";
		}
		//乙级
		else if("-2".equals(rank)){
			str +=uuid+"/-2";
		}
		//丙级
		else if("-3".equals(rank)){
			str +=uuid+"/-3";
		}
		//乙级及以上
		else if("-21".equals(rank)){
			str +=uuid+"/-1"+","+uuid+"/-2"+"','";
			str +=uuid+"/-1"+"','"+uuid+"/-2";
		}
		//丙级及以上
		else if("-31".equals(rank)){
			str +=uuid+"/-1"+","+uuid+"/-2"+","+uuid+"/-3"+"','";
			str +=uuid+"/-1"+","+uuid+"/-2"+"','";
			str +=uuid+"/-1"+"','"+uuid+"/-2"+"','"+uuid+"/-3";
		}
		//乙级及以上
		else if("u-2".equals(rank)){
			str +=uuid+"/-1"+","+uuid+"/-2"+"','";
			str +=uuid+"/-1"+"','"+uuid+"/-2";
		}
		//丙级及以上
		else if("u-3".equals(rank)){
			str +=uuid+"/-1"+","+uuid+"/-2"+","+uuid+"/-3"+"','";
			str +=uuid+"/-1"+"','"+uuid+"/-2"+"','";
			str +=uuid+"/-1"+"','"+uuid+"/-2"+"','"+uuid+"/-3";
		}
		return str;
	}

	/**
	 * 资质等级中文
	 * @param
	 * @param rank
	 * @return
	 */
	public static String spellRank(String rank) {
		String str="";
		//特级
		if("0".equals(rank)){
			str +="特级";
		}
		//一级
		else if("1".equals(rank)){
			str +="一级";
		}
		//二级
		else if("2".equals(rank)){
			str +="二级";
		}
		//三级
		else if("3".equals(rank)){
			str +="三级";
		}
		//一级及以上
		else if("11".equals(rank)){
			str +="一级及以上";
		}
		//二级及以上
		else if("21".equals(rank)){
			str +="二级及以上";
		}
		//三级及以上
		else if("31".equals(rank)){
			str +="三级及以上";
		}
		//甲级
		else if("-1".equals(rank)){
			str +="甲级";
		}
		//乙级
		else if("-2".equals(rank)){
			str +="乙级";
		}
		//丙级
		else if("-3".equals(rank)){
			str +="丙级";
		}
		//乙级及以上
		else if("-21".equals(rank)){
			str +="乙级及以上";
		}
		//丙级及以上
		else if("-31".equals(rank)){
			str +="丙级及以上";
		}

		return str;
	}

	public static String verificationCode() {
		String retStr = "";
		String strTable = true ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
		int len = strTable.length();
		boolean bDone = true;
		do {
		  retStr = "";
		  int count = 0;
		  for (int i = 0; i < 6; i++) {
			double dblR = Math.random() * len;
			int intR = (int) Math.floor(dblR);
			char c = strTable.charAt(intR);
			if (('0' <= c) && (c <= '9')) {
			 count++;
			}
			retStr += strTable.charAt(intR);
		  }
		  if (count >= 2) {
			bDone = false;
		   }
		} while (bDone);

		return retStr;
	}

	/**
	 * 根据对象的属性值计算出唯一的hash值。
	 * 支持对象的多层属性判断
	 * @param parameterObj
	 * @return 可返回负数
	 */
	public static int buildParamHash(Object parameterObj){
		int result =-1;
		String paramAttrs= buildObjectAttr(parameterObj);
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
			logger.error(e,e);
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

	public static String buildRSAAttr(Object parameterObj){
		StringBuffer sb = new StringBuffer("");
		try {
			Class clazz = parameterObj.getClass();
			Method[] methods = clazz.getMethods();

			for (Method method : methods) {
				String methodName = method.getName();
//				if(methodName!=null){System.out.println("方法名称:" + methodName);}
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
						if(value!=null){sb.append(value);}
					}else{
						Object value =method.invoke(parameterObj);
						if(value!=null){sb.append(buildRSAAttr(value));
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
			logger.error(e,e);
		}
		return sb.toString();
	}

	public static String ConvertObjectArrToStr(Object [] arr) {
		String result = "";
		if (arr != null && arr.length > 0) {
			for (int i = 0; i < arr.length; i++) {
				if (!"".equals(String.valueOf(arr[i]))) {
					result += String.valueOf(arr[i]) + ",";
				}
			}
			if (!"".equals(result)) {
				result = result.substring(0, result.length() - 1);
			}
		}
		return result;
	}

	/**
	 * 获取tabCode
	 * @param province
	 * @return
	 */
	public static String getCode(String province) {
		String tabCode = "";
		switch (province) {
			case "广西壮族自治区":
				tabCode = "guangx";
				break;
			case "江西省":
				tabCode = "jiangx";
				break;
			case "贵州省":
				tabCode = "guiz";
				break;
			case "吉林省":
				tabCode = "jil";
				break;
			case "河北省":
				tabCode = "hebei";
				break;
			case "四川省":
				tabCode = "sichuan";
				break;
			case "天津市":
				tabCode = "tianj";
				break;
			case "甘肃省":
				tabCode = "gans";
				break;
			case "黑龙江省":
				tabCode = "heilj";
				break;
			case "青海省":
				tabCode = "qingh";
				break;
			case "西藏自治区":
				tabCode = "xizang";
				break;
			case "安徽省":
				tabCode = "anh";
				break;
			case "北京市":
				tabCode = "beij";
				break;
			case "福建省":
				tabCode = "fuj";
				break;
			case "浙江省":
				tabCode = "zhej";
				break;
			case "河南省":
				tabCode = "henan";
				break;
			case "江苏省":
				tabCode = "jiangs";
				break;
			case "内蒙古自治区":
				tabCode = "neimg";
				break;
			case "宁夏回族自治区":
				tabCode = "ningx";
				break;
			case "山东省":
				tabCode = "shand";
				break;
			case "山西省":
				tabCode = "sanx";
				break;
			case "海南省":
				tabCode = "hain";
				break;
			case "上海市":
				tabCode = "shangh";
				break;
			case "广东省":
				tabCode = "guangd";
				break;
			case "新疆维吾尔自治区":
				tabCode = "xinjiang";
				break;
			case "云南省":
				tabCode = "yunn";
				break;
			case "陕西省":
				tabCode = "shanxi";
				break;
			case "湖北省":
				tabCode = "hubei";
				break;
			default:
				tabCode = null;
				break;
		}
		return tabCode;
	}
}