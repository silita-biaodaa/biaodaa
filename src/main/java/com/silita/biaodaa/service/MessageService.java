package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.dao.NoticeMapper;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.dao.TbMessageMapper;
import com.silita.biaodaa.dao.TbReplyCommentMapper;
import com.silita.biaodaa.model.Page;
import com.silita.biaodaa.model.TbMessage;
import com.silita.biaodaa.model.TbReplyComment;
import com.silita.biaodaa.utils.PushMessageUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    TbCompanyMapper tbCompanyMapper;

    /**
     * 添加回复评论消息
     */
    public Integer saveReplyCommentMessage(Integer replyId, String toUid) {
        TbMessage message = new TbMessage();
        message.setUserId(toUid);
        message.setIsRead(Integer.valueOf(Constant.info_female));
        message.setMsgType(Constant.MSG_TYPE_REPLY);
        message.setPushd(new Date());
        message.setReplyId(replyId.toString());
        tbMessageMapper.insert(message);
        return message.getPkid();
    }

    /**
     * 消息列表
     *
     * @param param
     * @return
     */
    public PageInfo listMessage(Map<String, Object> param) {
        String channel = VisitInfoHolder.getChannel();
        if ("1003".equals(channel)) {
            param.put("msgType", "subscribe");
        }
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
     *
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
     *
     * @param param
     */
    public void setIsRead(Map<String, Object> param) {
        String pkid = MapUtils.getString(param, "pkid");
        String[] ids = pkid.split(",");
        if (ids.length > 1) {
            Map<String, Object> value = new HashedMap();
            for (String id : ids) {
                value.put("pkid", id);
                tbMessageMapper.setIsRead(value);
            }
            return;
        }
        tbMessageMapper.setIsRead(param);
    }

    /**
     * 获取未读消息个数
     *
     * @return
     */
    public int getIsReadCount() {
        Map<String, Object> param = new HashedMap(2) {{
            put("userId", VisitInfoHolder.getUid());
        }};
        String channel = VisitInfoHolder.getChannel();
        if ("1003".equals(channel)) {
            param.put("msgType", "subscribe");
        }
        return tbMessageMapper.queryIsReadCount(param);
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
                    map.put("reCompany", replyComment.getReCompany());
                    map.put("reImage", replyComment.getReImage());
                    map.put("commentId", replyComment.getCommentId());
                    map.put("replyContent", replyComment.getReplyContent());
                    map.put("replyId", replyComment.getPkid());
                    map.put("noticeTitle", "");
                    if (StringUtils.isNotEmpty(replyComment.getSource())) {
                        map.put("noticeTitle", noticeMapper.queryNoticeTitle(map));
                    }
                    replyComment = null;
                } else if (Constant.MSG_TYPE_COMPANY.equals(map.get("msgType"))) {
                    map.put("comName", tbCompanyMapper.getCompany(MapUtils.getString(map, "replyId")).getComName());
                }
            }
        }
    }

    /**
     * 工商更新信息推送
     *
     * @param param
     */
    public void pushMessage(Map<String, Object> param, List<String> users) {
        String comId = MapUtils.getString(param, "comId");
        String comName = MapUtils.getString(param, "comName");
        TbMessage message;
        for (String user : users) {
            PushMessageUtils.pushMessage(user, param, "企业数据更新已完成", comName + "的工商信息已更新完成！", "company");
            message = new TbMessage();
            message.setPushd(new Date());
            message.setMsgType("company");
            message.setIsRead(Integer.valueOf(Constant.info_female));
            message.setMsgTitle("企业数据更新已完成");
            message.setMsgContent(comName + "的工商信息已更新完成！");
            message.setUserId(user);
            message.setReplyId(comId);
            tbMessageMapper.insert(message);
        }
    }

    /**
     * 添加消息
     *
     * @param userId  用户id
     * @param title   消息标题
     * @param content 消息内容
     * @param msgType 消息类型
     * @param replyId 关联id(可为空)
     */
    public void sendMessageSyetem(String userId, String title, String content, String msgType, String replyId) {
        TbMessage message = new TbMessage();
        message.setUserId(userId);
        message.setMsgType(msgType);
        message.setMsgTitle(title);
        message.setMsgContent(content);
        message.setReplyId(replyId);
        message.setIsRead(Integer.valueOf(Constant.info_female));
        message.setPushd(new Date());
        tbMessageMapper.insert(message);
    }
}
