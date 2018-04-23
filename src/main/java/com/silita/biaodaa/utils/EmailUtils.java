package com.silita.biaodaa.utils;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

/**
 * @Author:chenzhiqiang
 * @Date:2018/4/23 9:46
 * @Description: 邮件工具类
 */
public class EmailUtils {
    private static final Logger logger = Logger.getLogger(EmailUtils.class);

    public static final String SUCCESS_MSG = "邮件发送成功！";

    public static final String FAILURE_MSG = "邮件发送失败！";

    /**
     * 发送邮件
     * @param subject   邮件主题
     * @param message   邮件内容
     * @return
     */
    public static String sendEmail(String subject, String message){
        try {
            String senderName = PropertiesUtils.getProperty("sender.name");
            String senderPass = PropertiesUtils.getProperty("sender.pass");
            String receiverName = PropertiesUtils.getProperty("receiver.name");
            Preconditions.checkArgument(StringUtils.isNotBlank(senderName), "sender.name不能为空！");
            Preconditions.checkArgument(StringUtils.isNotBlank(senderPass), "sender.pass不能为空！");
            Preconditions.checkArgument(StringUtils.isNotBlank(receiverName), "receiver.name不能为空！");

            Email email = new SimpleEmail();
            email.setHostName("smtp.mxhichina.com");
            email.setSmtpPort(25);
            email.setAuthenticator(new DefaultAuthenticator(senderName, senderPass));
            email.setSSLOnConnect(true);
            email.setFrom(senderName);
            email.setSubject(subject);
            email.setMsg(message);
            email.addTo(receiverName);
            email.send();
            logger.info(String.format("邮件发送成功！主题%s,内容%s", subject, message));
        } catch (EmailException e) {
            logger.error(e.toString());
            return FAILURE_MSG;
        }
        return SUCCESS_MSG;
    }
}
