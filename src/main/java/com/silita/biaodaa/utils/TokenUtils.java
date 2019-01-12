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
        parameters.put("loginName", sysUser.getLoginName());
        parameters.put("userName", sysUser.getUserName());
        parameters.put("pkid", sysUser.getPkid());
        parameters.put("channel", sysUser.getChannel());
        parameters.put("phoneNo", sysUser.getPhoneNo());
        parameters.put("loginTime", String.valueOf(sysUser.getLoginTime()));
        parameters.put("tokenVersion",tokenVersion);
        parameters.put("roleCode",sysUser.getRoleCode());
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
        paramMap.put("loginName",jsonObject.getString("loginName"));
        paramMap.put("userName",jsonObject.getString("userName"));
        paramMap.put("pkid", jsonObject.getString("pkid"));
        paramMap.put("channel", jsonObject.getString("channel"));
        paramMap.put("phoneNo", jsonObject.getString("phoneNo"));
        paramMap.put("loginTime",jsonObject.getString("loginTime"));
        paramMap.put("tokenVersion",jsonObject.getString("tokenVersion"));
        paramMap.put("roleCode",jsonObject.getString("roleCode"));
        paramMap.put("permissions",jsonObject.getString("permissions"));
        return paramMap;
    }
}
