package com.silita.biaodaa.common;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sms.model.v20160927.SingleSendSmsRequest;
import com.aliyuncs.sms.model.v20160927.SingleSendSmsResponse;

/**
 * 阿里云短信Util
 * Created by 91567 on 2018/4/13.
 */
public class SendMessage {

    private final static String regionId = "cn-hangzhou";
    private final static String accessKeyId = "LTAIVLTf1eLK9MWJ";
    private final static String secret = "Ti9oFBqhbVXu3un5AnpR908SObVAAe";

    /**
     * 注册模板
     * 模板为验证码${code}，您正在注册成为${product}用户，感谢您的支持！
     */
    public static String registerCode(String code,String phone) {
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, secret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Sms",  "sms.aliyuncs.com");
            IAcsClient client = new DefaultAcsClient(profile);
            SingleSendSmsRequest request = new SingleSendSmsRequest();
            request.setSignName("标大大biaodaa");
            request.setTemplateCode("SMS_104720006");
            //短信模板中的变量；数字需要转换为字符串；个人用户每个变量长度必须小于15个字符。 例如:短信模板为：“接受短信验证码${no}”,此参数传递{“no”:”123456”}，用户将接收到[短信签名]接受短信验证码123456
            request.setParamString("{\"code\":\""+code+"\",\"product\":\"标大大\"}");
            request.setRecNum(phone);
            SingleSendSmsResponse httpResponse = client.getAcsResponse(request);
        }catch (ServerException e) {
            e.printStackTrace();
        }catch (ClientException e) {
            if("InvalidRecNum.Malformed".equals(e.getErrCode())){
                return "获取失败，手机号不正确！";
            }else if("InvalidSendSms".equals(e.getErrCode())){
                return "当前操作频繁，请稍后再试！";
            }else{
                return "验证码获取失败！";
            }
        }
        return "0";
    }

    /**
     * 修改密码
     * 验证码${code},您正在尝试修改${product}登录密码，请妥善保管账号信息
     * @param code
     * @param phone
     * @return
     */
    public static String updatePasswdCode(String code,String phone) {
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, secret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Sms",  "sms.aliyuncs.com");
            IAcsClient client = new DefaultAcsClient(profile);
            SingleSendSmsRequest request = new SingleSendSmsRequest();
            request.setSignName("标大大biaodaa");
            request.setTemplateCode("SMS_104665007");
            //短信模板中的变量；数字需要转换为字符串；个人用户每个变量长度必须小于15个字符。 例如:短信模板为：“接受短信验证码${no}”,此参数传递{“no”:”123456”}，用户将接收到[短信签名]接受短信验证码123456
            request.setParamString("{\"code\":\""+code+"\",\"product\":\"标大大\"}");
            request.setRecNum(phone);
            SingleSendSmsResponse httpResponse = client.getAcsResponse(request);
        }catch (ServerException e) {
            e.printStackTrace();
        }catch (ClientException e) {
            if("InvalidRecNum.Malformed".equals(e.getErrCode())){
                return "获取失败，手机号不正确！";
            }else if("InvalidSendSms".equals(e.getErrCode())){
                return "当前操作频繁，请稍后再试！";
            }else{
                return "验证码获取失败！";
            }
        }
        return "0";
    }
}
