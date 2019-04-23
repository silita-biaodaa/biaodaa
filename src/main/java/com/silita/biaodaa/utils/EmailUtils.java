package com.silita.biaodaa.utils;

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

    // 发件人默认邮箱
    public static final String SENDER_DEFAULT_NAME = "biaodaa@biaodaa.com";
    // 发件人默认密码
    public static final String SENDER_DEFAULT_PASS = "Yb20141125";
    // 收件人默认邮箱
    public static final String RECEIVER_DEFAULT_NAME = "chenyi@yaobangjs.com";

    /**
     * 发送邮件
     * @param subject   邮件主题
     * @param message   邮件内容
     * @param receiver  收件人
     * @return
     */
    public static String sendEmail(String subject, String message, String receiver){
        try {
            String senderName = PropertiesUtils.getProperty("sender.name");
            String senderPass = PropertiesUtils.getProperty("sender.pass");
            if (StringUtils.isBlank(senderName)) {
                senderName = SENDER_DEFAULT_NAME;
            }
            if (StringUtils.isBlank(senderPass)) {
                senderName = SENDER_DEFAULT_PASS;
            }
            if (StringUtils.isBlank(receiver)) {
                receiver = RECEIVER_DEFAULT_NAME;
            }
            Email email = new SimpleEmail();
            email.setHostName("smtp.mxhichina.com");
            email.setSmtpPort(465);
            email.setAuthenticator(new DefaultAuthenticator(senderName, senderPass));
            email.setSSLOnConnect(true);
            email.setFrom(senderName);
            email.setSubject(subject);
            email.setMsg(message);
            email.addTo(receiver);
            email.send();
            logger.info(String.format("邮件发送成功！发件人：%s，收件人：%s，主题%s，内容%s", senderName, receiver, subject, message));
        } catch (EmailException e) {
            logger.error(e.toString());
            return FAILURE_MSG;
        }
        return SUCCESS_MSG;
    }
}
