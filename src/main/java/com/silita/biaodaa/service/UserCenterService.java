package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.MessagePushMapper;
import com.silita.biaodaa.model.MessagePush;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/12.
 */
@Service("userCenterService")
public class UserCenterService {

    @Autowired
    private MessagePushMapper messagePushMapper;

    public PageInfo queryMessageList(Page page, Map params){
        List<MessagePush> messagePushes = new ArrayList<>();

        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        messagePushes = messagePushMapper.listMessageByUserIdAndType(params);
        PageInfo pageInfo = new PageInfo(messagePushes);
        return pageInfo;
    }

}
