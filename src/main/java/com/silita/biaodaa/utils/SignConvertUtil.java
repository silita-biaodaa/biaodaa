package com.silita.biaodaa.utils;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/***
 * ApiGateway sign 生成
 */
public class SignConvertUtil {
    private static  final  Logger LOGGER = Logger.getLogger(SignConvertUtil.class);
    public static String generateSign(String callerService, String contextPath, String version, String timestamp, String serviceSecret, String requestPath) {
        String sign = "";
        if(callerService == null || contextPath == null || timestamp == null || serviceSecret == null) {
            return sign;
        }
        Map<String, String> map = new LinkedHashMap<>();
        map.put("callerService", callerService);
        map.put("contextPath", contextPath);
        if(requestPath != null) {
            map.put("requestPath", requestPath);
        }
        map.put("timestamp", timestamp);
        map.put("v", version);
        try {
            sign = generateMD5Sign(serviceSecret, map);
        } catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
        return sign;
    }



    public static String generateMD5Sign(String secret, Map<String, String> parameters) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = md5.digest(generateConcatSign(secret, parameters).getBytes("utf-8"));
        return byteToHex(bytes);
    }

    private static String generateConcatSign(String secret, Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder().append(secret);
        Set<String> keys = parameters.keySet();
        for(String key : keys) {
            sb.append(key).append(parameters.get(key));
        }
        return sb.append(secret).toString();
    }

    private static String byteToHex(byte[] bytesIn) {
        StringBuilder sb = new StringBuilder();
        for(byte byteIn : bytesIn) {
            String bt = Integer.toHexString(byteIn & 0xff);
            if(bt.length() == 1)
                sb.append(0).append(bt);
            else
                sb.append(bt);
        }
        return sb.toString().toUpperCase();
    }
    /**
     * @description： SHA、SHA1加密
     * @parameter：   str：待加密字符串
     * @return：  加密串
     **/
    public static String SHA1(String str) {
        try {
            MessageDigest digest = MessageDigest
                    .getInstance("SHA-1"); //如果是SHA加密只需要将"SHA-1"改成"SHA"即可
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexStr = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexStr.append(0);
                }
                hexStr.append(shaHex);
            }
            return hexStr.toString();

        } catch (Exception e) {
            LOGGER.error("SHA-1加密异常", e);
        }
        return null;
    }
}
