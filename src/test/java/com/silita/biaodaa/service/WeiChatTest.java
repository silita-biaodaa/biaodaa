package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.utils.HttpUtils;
import com.silita.biaodaa.utils.PropertiesUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhushuai on 2019/9/16.
 */
public class WeiChatTest{

    @org.junit.Test
    public void test() {
        String token = null;
        String appid = PropertiesUtils.getProperty("appid");
        String requestUrl = PropertiesUtils.getProperty("access_token_url");
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("appid", appid);
        String parameterJson = JSONObject.toJSONString(parameter);
        String result = HttpUtils.connectURL(requestUrl, parameterJson, "POST");
        JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
        Integer status = jsonObject.getInteger("status");
        if (status != null && status.intValue() == 1) {
            JSONObject accessToken = (JSONObject) jsonObject.get("accessToken");
            if (accessToken != null && accessToken.getString("accessToken") != null) {
                token = accessToken.getString("accessToken");
            }
        }
        System.out.println(token);
    }

}
