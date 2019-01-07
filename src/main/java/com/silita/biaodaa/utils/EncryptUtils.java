package com.silita.biaodaa.utils;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.common.Constant;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Map;

/**
 * Created by dh on 2019/1/4.
 */
public class EncryptUtils {
    private static Logger logger = Logger.getLogger(EncryptUtils.class);

    /**
     * 加密，生成token（token版本号.用户信息.sign）
     * @param tokenVersion
     * @param parameters
     * @param secret
     * @return
     */
    public static String  encryptToken(String tokenVersion,Map<String, String> parameters,String secret){
        StringBuffer token = new StringBuffer();
        String sign =null;
        String parameterJson = JSONObject.toJSONString(parameters);
        try {
            tokenVersion = Base64Encode(tokenVersion);
            parameterJson = Base64Encode(parameterJson);
            sign = SignConvertUtil.generateMD5Sign(secret, parameters);
        }catch (Exception e){
            logger.error(e,e);
        }
        token.append(tokenVersion).append(".");
        token.append(parameterJson).append(".");
        token.append(sign);
        return token.toString();
    }


    public static String Base64Decode(String s) throws UnsupportedEncodingException {
        return new String(Base64.getDecoder().decode(s), Constant.STR_ENCODING);
    }

    public static String Base64Encode(String s) throws UnsupportedEncodingException {
        return Base64.getEncoder().encodeToString(s.getBytes(Constant.STR_ENCODING));
    }
}
