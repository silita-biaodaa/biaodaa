package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.dao.TbMessageMapper;
import com.silita.biaodaa.model.Page;
import com.silita.biaodaa.model.TbMessage;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhushuai on 2019/5/24.
 */
@Service
public class MessageService {

    @Autowired
    TbMessageMapper tbMessageMapper;

    /**
     * 添加回复评论消息
     */
    public void saveReplyCommentMessage(Integer replyId, String toUid) {
        TbMessage message = new TbMessage();
        message.setUserId(toUid);
        message.setIsRead(Integer.valueOf(Constant.info_female));
        message.setMsgType(Constant.MSG_TYPE_REPLY);
        message.setPushd(new Date());
        message.setReplyId(replyId);
        tbMessageMapper.insert(message);
    }

    /**
     * 消息列表
     *
     * @param param
     * @return
     */
    public PageInfo listMessage(Map<String, Object> param) {
        Integer pageIndex = MapUtils.getInteger(param, "pageNum");
        Integer pageSize = MapUtils.getInteger(param, "pageSize");
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageIndex);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map<String, Object>> msgList = tbMessageMapper.queryMessageList(param);
        this.setReplyCommentMsg(msgList);
        PageInfo pageInfo = new PageInfo(msgList);
        return pageInfo;
    }

    private void setReplyCommentMsg(List<Map<String, Object>> list) {
        if (null != list && list.size() > 0) {
            for (Map<String,Object> map : list){
                if (Constant.MSG_TYPE_REPLY.equals(map.get("msgType"))){

                }
            }
        }
    }
}
