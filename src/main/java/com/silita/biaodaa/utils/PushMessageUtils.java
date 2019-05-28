package com.silita.biaodaa.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;

/**
 * Created by zhushuai on 2019/5/28.
 */
public class PushMessageUtils {

    private static Logger logger = LoggerFactory.getLogger(PushMessageUtils.class);


    private static String pushUrl = "http://cloudpush.aliyuncs.com";

    /**
     * 推送App消息
     * @param userId
     */
    public static void pushMessage(String userId) {
        StringBuffer sbf = new StringBuffer();
        try {
            sbf.append("Action=").append("push").append("&").append("Target=").append("24610908").append("&").append("TargetValue=")
                    .append(userId).append("&").append("DeviceType=").append("ALL").append("&").append("PushType=")
                    .append("NOTICE").append("&").append("Title=").append(URLEncoder.encode("评论", "UTF-8")).append("&").append("Body=").append(URLEncoder.encode("你有新的回复信息", "UTF-8"));
            String result = HttpUtils.sendGet(pushUrl, sbf.toString());
            logger.info("推送消息成功!",result);
        } catch (Exception e) {
            logger.info("推送消息失败！",e);
            return ;
        }
    }
}
