package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.WeixinService;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by zhangxiahui on 17/6/13.
 */
@Controller
@RequestMapping("/wx")
public class WeixinController {

    private static final Logger logger = Logger.getLogger(WeixinController.class);

    @Autowired
    WeixinService weixinService;

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getTestID(HttpServletRequest request, HttpServletResponse response) {
        logger.info("接入成功");
        String token = PropertiesUtils.getProperty("wx_token");//在开发者的测试号管理页面填写的token

        //获取请求中的参数，由于测试号没有加解密功能，所以此处并不需要解密，直接获取
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String weixinSignature = request.getParameter("signature");
        String echostr = request.getParameter("echostr");
        logger.info("参数：token:"+token+",timestamp:"+timestamp+",nonce:"+nonce+",signature:"+weixinSignature);

        //对参数进行字典序排序
        String[] para = {token,timestamp,nonce};
        if(timestamp!=null&&nonce!=null){
            Arrays.sort(para);

            //排序之后转换成字符串
            String paraString = "";
            for(int i=0;i<para.length;i++){
                paraString += para[i];
            }
            logger.info("排序结果："+paraString);

            //sha1加密工具(java.security.MessageDigest)
            MessageDigest messageDigest = null;
            try {
                //获取加密工具对象
                messageDigest = MessageDigest.getInstance("SHA-1");
                //加密并转换为16进制字符串，byteToStr方法详细内容见下方
                String mySignature = byteToStr(messageDigest.digest(paraString.getBytes()));
                if(mySignature.equals(weixinSignature.toUpperCase())){
                    logger.info("==========验证成功============");
                    //原样返回微信服务器发送的echostr参数，同样，不需要加密
                    return echostr;
                }
            } catch (NoSuchAlgorithmException e) {
                logger.error("获取加密工具对象失败",e);
            }
        }

        return "认证失败";
    }

    /**
     * 将字节转换为十六进制字符串
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];
        String s = new String(tempArr);
        return s;

    }
    /**
     * 将字节数组转换为十六进制字符串
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {

        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;

    }

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST)
    public String replyTextMessage(HttpServletRequest request,HttpServletResponse response){
        return weixinService.processRequest(request);
    }
}
