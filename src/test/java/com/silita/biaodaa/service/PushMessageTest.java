package com.silita.biaodaa.service;

import com.silita.biaodaa.utils.HttpUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by zhushuai on 2019/5/28.
 */
public class PushMessageTest {

    private static String pushUrl = "http://cloudpush.aliyuncs.com";

    @org.junit.Test
    public void push() throws UnsupportedEncodingException {
        StringBuffer sbf = new StringBuffer();
        sbf.append("Action=").append("push").append("&").append("Target=").append("24610908").append("&").append("TargetValue=")
                .append("8073b63c941c11e5b833f0795939bee2").append("&").append("DeviceType=").append("ALL").append("&").append("PushType=")
                .append("NOTICE").append("&").append("Title=").append(URLEncoder.encode("评论","UTF-8")).append("&").append("Body=").append(URLEncoder.encode("你有新的回复信息","UTF-8"));
        System.out.println(HttpUtils.sendGet(pushUrl, sbf.toString()));
    }

}
