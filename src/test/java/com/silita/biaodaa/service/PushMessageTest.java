package com.silita.biaodaa.service;

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

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by zhushuai on 2019/5/28.
 */
public class PushMessageTest {


    @org.junit.Test
    public void push() throws UnsupportedEncodingException {
        String accessKey = "LTAIVLTf1eLK9MWJ";
        String accessKeySecret = "Ti9oFBqhbVXu3un5AnpR908SObVAAe";
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKey, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        PushRequest pushRequest  = new PushRequest();
        pushRequest.setProtocol(ProtocolType.HTTPS);
        pushRequest.setMethod(MethodType.POST);
        pushRequest.setAppKey(24610908L);
        pushRequest.setTarget("ACCOUNT");
        pushRequest.setTargetValue("eb8154b8e47e416790ee6438430f3c21");
        pushRequest.setPushType("NOTICE");
        pushRequest.setDeviceType("ALL");
        pushRequest.setTitle("评论");
        pushRequest.setBody("你有新的回复信息");
        pushRequest.setIOSApnsEnv("DEV");
        Map<String,Object> param = new HashedMap(){{
            put("noticeType","reply");
            put("url","https://www.baidu.com");
        }};
        Map<String,Object> paramters = new HashedMap(){{
           put("data",param);
        }};
        pushRequest.setIOSExtParameters(JSONObject.toJSONString(paramters));
        try {
            PushResponse pushResponse = client.getAcsResponse(pushRequest);
            System.out.printf("RequestId: %s, MessageID: %s\n",
                    pushResponse.getRequestId(), pushResponse.getMessageId());
        } catch (ClientException e) {
            e.printStackTrace();
        }

    }

}
