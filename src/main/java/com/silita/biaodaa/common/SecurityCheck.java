package com.silita.biaodaa.common;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.utils.PropertiesUtils;
import com.silita.biaodaa.utils.SignConvertUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

/**
 * Created by zhangxiahui on 17/7/26.
 */
public class SecurityCheck {

    private static final Logger logger = Logger.getLogger(SecurityCheck.class);

    public static String getCookieValue(HttpServletRequest request, String name) {
        String value = null;
        if(request != null && request.getCookies() != null) {
            for(Cookie ck : request.getCookies()) {
                if(ck.getName().contains(name)) {
                    value = ck.getValue();
                }
            }
        }
        return value;
    }

    public static String getHeaderValue(HttpServletRequest request, String name) {
        String value = null;
        if(request != null) {
            value = request.getHeader(name);
        }
        return value;
    }

    public static boolean checkSigner(Map<String, String> parameters, String contentSign) {
        boolean alongBoo = false;
        String sign = "";
        String secret = PropertiesUtils.getProperty("CONTENT_SECRET");
        if(parameters != null && contentSign != null) {
            try {
                sign = SignConvertUtil.generateMD5Sign(secret, parameters);
            } catch(NoSuchAlgorithmException e) {
                logger.error(e.getMessage(), e);
            } catch(UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
            if(contentSign.equals(sign)) {
                //鉴权成功
                alongBoo = true;
            }
        }
        return alongBoo;
    }


    public static boolean checkUserSigner(String xToken) {
        boolean alongBoo = false;
        String sign = null;
        String name = null;
        String pwd = null;
        String showName = null;
        String userId = null;
        Map<String, String> parameters = new HashedMap();
        if(xToken!=null){
            String [] token = xToken.split("\\.");
            if(token.length==2){
                sign = token[0];
                String json = null;
                try {
                    json = new String(Base64.getDecoder().decode(token[1]),"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                JSONObject jsonObject = (JSONObject) JSONObject.parse(json);
                name = jsonObject.getString("name");
                pwd = jsonObject.getString("pwd");
                showName = jsonObject.getString("showName");
                userId = jsonObject.getString("userId");
                parameters.put("name", name);
                parameters.put("pwd", pwd);
                parameters.put("showName", showName);
                parameters.put("userId",userId);
                alongBoo = SecurityCheck.checkSigner(parameters,sign);
            }
        }
        return alongBoo;
    }



    private static String getSign(String accessUrl, String callerService, String serviceSecret, String timestamp) {
        String[] strs = StringUtils.split(accessUrl, "/");
        //上下文
        String contextPath = strs[2];
        //版本号
        String version = strs[3];
        //        String[] requestParts = accessUrl.split("/\\d[.]\\d/");
        //请求路径
        String requestPath = StringUtils.substringAfter(accessUrl, version);

        return SignConvertUtil.generateSign(callerService, contextPath, version, timestamp, serviceSecret, requestPath);
    }




}
