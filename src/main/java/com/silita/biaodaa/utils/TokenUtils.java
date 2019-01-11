package com.silita.biaodaa.utils;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.model.SysUser;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户token操作类
 */
public class TokenUtils {

    /**
     * 生成用户token （token版本号.用户信息.sign）
     * @param sysUser
     * @return
     */
    public static String buildToken(SysUser sysUser){
        String secret = PropertiesUtils.getProperty("CONTENT_SECRET");
        String tokenVersion = PropertiesUtils.getProperty("token.version");

        Map<String, String> parameters = new HashMap();
        parameters.put("login_name", sysUser.getLogin_name());
        parameters.put("user_name", sysUser.getUser_name());
        parameters.put("pkid", sysUser.getPkid());
        parameters.put("channel", sysUser.getChannel());
        parameters.put("phone_no", sysUser.getPhone_no());
        parameters.put("login_time", String.valueOf(sysUser.getLoginTime()));
        parameters.put("tokenVersion",tokenVersion);
        parameters.put("role_code",sysUser.getRole_code());
        parameters.put("permissions",sysUser.getPermissions());
        return EncryptUtils.encryptToken(tokenVersion,parameters,secret);
    }


    /**
     * 从token中解析用户信息
     * @param paramJson
     * @return
     */
    public static Map<String, String> parseJsonString(String paramJson){
        JSONObject jsonObject = (JSONObject) JSONObject.parse(paramJson);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("login_name",jsonObject.getString("login_name"));
        paramMap.put("user_name",jsonObject.getString("user_name"));
        paramMap.put("pkid", jsonObject.getString("pkid"));
        paramMap.put("channel", jsonObject.getString("channel"));
        paramMap.put("phone_no", jsonObject.getString("phone_no"));
        paramMap.put("login_time",jsonObject.getString("login_time"));
        paramMap.put("tokenVersion",jsonObject.getString("tokenVersion"));
        paramMap.put("role_code",jsonObject.getString("role_code"));
        paramMap.put("permissions",jsonObject.getString("permissions"));
        return paramMap;
    }
}
