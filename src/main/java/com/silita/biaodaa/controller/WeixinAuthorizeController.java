package com.silita.biaodaa.controller;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.model.weixin.JsapiSignature;
import com.silita.biaodaa.service.WeixinService;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangxiahui on 17/6/16.
 */
@Controller
@RequestMapping("/wxAuth")
public class WeixinAuthorizeController extends BaseController {

    private static final Logger logger = Logger.getLogger(WeixinAuthorizeController.class);

    @Autowired
    WeixinService weixinService;

    /**
     * 获取签证
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/fetchJsApiTicket", method = RequestMethod.POST)
    public Map<String, Object> fetchJsApiTicket(@RequestBody Map<String, Object> param) {
        String appid = PropertiesUtils.getProperty("appid");
        String url = MapUtils.getString(param, "url");
        String decoderUrl;
        try {
            decoderUrl = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            decoderUrl = url;
        }
        String nonceStr = null;
        String timestamp = null;
        String signature = null;
        if (decoderUrl != null) {
            String[] urls = decoderUrl.split("#");
            url = urls[0];
            JsapiSignature jsapiSignature = weixinService.fetchJsapiSignature(url);
            if (jsapiSignature != null) {
                nonceStr = jsapiSignature.getNoncestr();
                timestamp = jsapiSignature.getTimestamp();
                signature = jsapiSignature.getSignature();
            }
        }
        Map<String, Object> signMap = new HashMap<String, Object>() {{
            put("appId", appid);
        }};
        signMap.put("nonceStr", nonceStr);
        signMap.put("timestamp", timestamp);
        signMap.put("signature", signature);
        Map resultMap = new HashedMap(3);
        successMsg(resultMap, signMap);
        return resultMap;
    }

    /**
     * 绑定手机号
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/loginBindingUser", method = RequestMethod.POST)
    public Map<String, Object> loginBindingUser(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashedMap(3);
        try {
            return weixinService.bindingUser(param);
        } catch (Exception e) {
            logger.error("绑定失败！", e);
            resultMap.put("code", Constant.EXCEPTION_CODE);
            resultMap.put("msg", e.getMessage());
            return resultMap;
        }
    }

    /**
     * 注册并绑定用户
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/registerBindingUser", method = RequestMethod.POST)
    public Map<String, Object> registerBindingUser(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashedMap(3);
        try {
            return weixinService.registerBindingUser(param);
        } catch (Exception e) {
            logger.error("绑定注册失败！", e);
            resultMap.put("code", Constant.EXCEPTION_CODE);
            resultMap.put("msg", e.getMessage());
            return resultMap;
        }
    }

    /**
     * 登录
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/loginUser", method = RequestMethod.POST)
    public Map<String, Object> loginUser(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashedMap(3);
        try {
            return weixinService.loginUser(param);
        } catch (Exception e) {
            logger.error("登录失败！", e);
            resultMap.put("code", Constant.EXCEPTION_CODE);
            resultMap.put("msg", e.getMessage());
            return resultMap;
        }
    }


    /**
     * 解绑
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/untieUser", method = RequestMethod.POST)
    public Map<String, Object> untieUser(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashedMap(3);
        try {
            successMsg(resultMap);
            weixinService.untieUser();
            return resultMap;
        } catch (Exception e) {
            logger.error("解绑失败！", e);
            resultMap.put("code", Constant.EXCEPTION_CODE);
            resultMap.put("msg", e.getMessage());
            return resultMap;
        }
    }

}
