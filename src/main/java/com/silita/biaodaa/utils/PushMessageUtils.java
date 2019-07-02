package com.silita.biaodaa.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.push.model.v20160801.PushRequest;
import com.aliyuncs.push.model.v20160801.PushResponse;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by zhushuai on 2019/5/28.
 */
public class PushMessageUtils {

    private static Logger logger = LoggerFactory.getLogger(PushMessageUtils.class);

    /**
     * 推送App消息
     *
     * @param userId
     */
    public static void pushMessage(String userId, Map<String, Object> param,String title,String body,String noticeType) {
        String accessKey = "LTAIVLTf1eLK9MWJ";
        String accessKeySecret = "Ti9oFBqhbVXu3un5AnpR908SObVAAe";
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKey, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        PushRequest pushRequest = new PushRequest();
        pushRequest.setProtocol(ProtocolType.HTTPS);
        pushRequest.setMethod(MethodType.POST);
        pushRequest.setAppKey(24610908L);
        pushRequest.setTarget("ACCOUNT");
        pushRequest.setTargetValue(userId);
        pushRequest.setPushType("NOTICE");
        pushRequest.setDeviceType("ALL");
        pushRequest.setTitle(title);
        pushRequest.setBody(body);
        pushRequest.setIOSApnsEnv(PropertiesUtils.getProperty("push.env"));
        param.put("noticeType", noticeType);
        Map<String, Object> paramters = new HashedMap() {{
            put("data", param);
        }};
        pushRequest.setIOSExtParameters(JSONObject.toJSONString(paramters));
        pushRequest.setAndroidExtParameters(JSONObject.toJSONString(paramters));
        try {
            PushResponse pushResponse = client.getAcsResponse(pushRequest);
            System.out.printf("RequestId: %s, MessageID: %s\n",
                    pushResponse.getRequestId(), pushResponse.getMessageId());
            logger.info("消息推送成功--RequestId: %s, MessageID: %s\n",
                    pushResponse.getRequestId(), pushResponse.getMessageId());
        } catch (ClientException e) {
            logger.error("消息推送失败！",e);
        }
    }
}
