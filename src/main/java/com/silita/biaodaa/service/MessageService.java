package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.dao.NoticeMapper;
import com.silita.biaodaa.dao.TbMessageMapper;
import com.silita.biaodaa.dao.TbReplyCommentMapper;
import com.silita.biaodaa.model.Page;
import com.silita.biaodaa.model.TbMessage;
import com.silita.biaodaa.model.TbReplyComment;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
    @Autowired
    TbReplyCommentMapper tbReplyCommentMapper;
    @Autowired
    NoticeMapper noticeMapper;

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

    /**
     * 删除
     * @param param
     */
    public void delMsg(Map<String, Object> param) {
        String ids = MapUtils.getString(param, "ids").trim();
        List<String> pkids = Arrays.asList(ids.split(","));
        param.put("list", pkids);
        tbMessageMapper.deleteMsg(param);
    }

    /**
     * 设置消息已读状态
     * @param param
     */
    public void setIsRead(Map<String,Object> param){
        String pkid = MapUtils.getString(param,"pkid");
        String[] ids = pkid.split(",");
        if (ids.length > 1){
            Map<String,Object> value = new HashedMap();
            for (String id : ids){
                value.put("pkid",id);
                tbMessageMapper.setIsRead(value);
            }
            return;
        }
        tbMessageMapper.setIsRead(param);
    }

    /**
     * 获取未读消息个数
     * @return
     */
    public int getIsReadCount(){
        return tbMessageMapper.queryIsReadCount(VisitInfoHolder.getUid());
    }

    /**
     * 如果消息类型为回复评论类的需查询回复详情
     *
     * @param list
     */
    private void setReplyCommentMsg(List<Map<String, Object>> list) {
        if (null != list && list.size() > 0) {
            TbReplyComment replyComment;
            for (Map<String, Object> map : list) {
                if (Constant.MSG_TYPE_REPLY.equals(map.get("msgType"))) {
                    replyComment = tbReplyCommentMapper.queryReplyComment(MapUtils.getInteger(map, "replyId"));
                    map.put("source", replyComment.getSource());
                    map.put("relatedId", replyComment.getRelatedId());
                    map.put("relatedType", replyComment.getRelatedType());
                    map.put("replyUid", replyComment.getReplyUid());
                    map.put("toUid", replyComment.getToUid());
                    map.put("reNikename", replyComment.getReNikeName());
                    map.put("reCompany",replyComment.getReCompany());
                    map.put("reImage",replyComment.getReImage());
                    map.put("commentId", replyComment.getCommentId());
                    map.put("replyContent", replyComment.getReplyContent());
                    map.put("noticeTitle", noticeMapper.queryNoticeTitle(map));
                    replyComment = null;
                }
            }
        }
    }
}
