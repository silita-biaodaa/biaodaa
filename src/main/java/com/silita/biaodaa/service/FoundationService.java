package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.CarouselImage;
import com.silita.biaodaa.model.TbHotWords;
import com.silita.biaodaa.utils.EmailUtils;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/12.
 */
@Service("foundationService")
public class FoundationService {
    @Autowired
    private CarouselImageMapper carouselImageMapper;
    @Autowired
    private TbHotWordsMapper hotWordsMapper;
    @Autowired
    private VersionMapper versionMapper;
    @Autowired
    private FeedbackMapper feedbackMapper;
    @Autowired
    private BorrowMapper borrowMapper;

    /**
     * 根据showType取得全部的banner图
     * @param params
     * @return
     */
    public List<CarouselImage> queryCarouselImageList(Map<String, Object> params) {
        return carouselImageMapper.listCarouselImageByTypeAndShowType(params);
    }

    public List<TbHotWords> queryHotWordsByTypeList(Map<String, Object> params) {
        return hotWordsMapper.listHotWordsByType(params);
    }

    /**
     * 获得版本信息
     * @param loginChannel  登录频道
     * @return
     */
    public String getVersion(String loginChannel) {
        return versionMapper.getVersion(loginChannel);
    }

    /**
     * 添加意见反馈并邮件通知
     * @param params
     */
    public void addFeedback(Map<String, Object> params) {
        feedbackMapper.insertFeedback(params);
        String problem = (String)params.get("problem");
        String type = (String)params.get("type");
        String loginChannel = (String)params.get("loginChannel");
        String version = (String)params.get("version");
        String datetime = MyDateUtils.getTime(MyDateUtils.datetimePattern);
        String subject = "用户意见反馈邮件通知";
        String message = String.format("【" + (loginChannel.equals("1001") ? "Android" : "IOS") + "用户于 %s 提交了一条意见反馈。app版本：%s，问题类型：%s，问题内容：%s", datetime, version, type, problem);
        String receiver = PropertiesUtils.getProperty("receiver.name.feedback");
        if (StringUtils.isBlank(receiver)) {
            receiver = EmailUtils.RECEIVER_DEFAULT_NAME;
        }
        EmailUtils.sendEmail(subject, message, receiver);
    }

    /**
     * 添加保证金借款并邮件提醒
     * @param params
     */
    public void addBorrow(Map<String, Object> params){
        borrowMapper.insertBorrow(params);
        String borrower = (String)params.get("borrower");
        String projName = (String)params.get("projName");
        String phone = (String)params.get("phone");
        String datetime = MyDateUtils.getTime(MyDateUtils.datetimePattern);
        String subject = String.format("【%s】申请保证金借款", borrower);
        String message = String.format("借款人【%s】于 %s 发起了一笔保证金借款申请。借款项目为：%s，手机号码：%s", borrower, datetime, projName, phone);
        String receiver = PropertiesUtils.getProperty("receiver.name.borrow");
        if (StringUtils.isBlank(receiver)) {
            receiver = EmailUtils.RECEIVER_DEFAULT_NAME;
        }
        EmailUtils.sendEmail(subject, message, receiver);
    }
}
