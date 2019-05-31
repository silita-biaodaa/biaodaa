package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.odianyun.util.sensi.SensitiveFilter;
import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.dao.TbCommentInfoMapper;
import com.silita.biaodaa.dao.TbReplyCommentMapper;
import com.silita.biaodaa.dao.UserTempBddMapper;
import com.silita.biaodaa.model.Page;
import com.silita.biaodaa.model.SysUser;
import com.silita.biaodaa.model.TbCommentInfo;
import com.silita.biaodaa.model.TbReplyComment;
import com.silita.biaodaa.utils.PushMessageUtils;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhushuai on 2019/5/22.
 */
@Service
public class CommentService {

    @Autowired
    TbCommentInfoMapper tbCommentInfoMapper;
    @Autowired
    TbReplyCommentMapper tbReplyCommentMapper;
    @Autowired
    UserTempBddMapper userTempBddMapper;
    @Autowired
    TbCompanyService tbCompanyService;
    @Autowired
    MessageService messageService;

    /**
     * 查询评论列表
     *
     * @param param
     * @return
     */
    public PageInfo listComment(Map<String, Object> param) {
        Integer pageIndex = MapUtils.getInteger(param, "pageNum");
        Integer pageSize = MapUtils.getInteger(param, "pageSize");
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageIndex);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map<String, Object>> commentList = tbCommentInfoMapper.queryCommentList(param);
        this.setReplyComment(commentList, param);
        PageInfo pageInfo = new PageInfo(commentList);
        return pageInfo;
    }

    /**
     * 查询单个评论
     *
     * @param param
     * @return
     */
    public Map<String, Object> singleComment(Map<String, Object> param) {
        List<Map<String, Object>> commentList = tbCommentInfoMapper.querySingleCommentList(param);
        this.setReplyComment(commentList, param);
        return commentList.get(0);
    }

    /**
     * 与我相关评论
     *
     * @param param
     * @return
     */
    public PageInfo listUserComment(Map<String, Object> param) {
        Integer pageIndex = MapUtils.getInteger(param, "pageNum");
        Integer pageSize = MapUtils.getInteger(param, "pageSize");
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageIndex);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map<String, Object>> commentList = tbCommentInfoMapper.queryUserCommentList(param);
        this.setReplyComment(commentList, param);
        PageInfo pageInfo = new PageInfo(commentList);
        return pageInfo;
    }

    /**
     * 评论/回复评论
     *
     * @param param
     * @return
     */
    public Map<String, Object> pushComment(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashedMap();
        resultMap.put("code", Constant.SUCCESS_CODE);
        resultMap.put("msg", "操作成功！");
        String content = MapUtils.getString(param, "content");
        //是否包含敏感词汇
        SensitiveFilter filter = SensitiveFilter.DEFAULT;
        String replceStr = filter.filter(content, '*');
        if (!content.equalsIgnoreCase(replceStr)) {
            resultMap.put("code", Constant.ERR_COMMTENT_MINGAN);
            resultMap.put("msg", "包含敏感词汇，请重新输入！");
            return resultMap;
        }
        String commContent = MapUtils.getString(param, "content");
        param.put("commContent", EmojiParser.parseToAliases(commContent));
        //获取用户信息
        String userId = MapUtils.getString(param, "userId");
        SysUser commentUser = userTempBddMapper.queryUserDetailById(userId);
        //判断回复评论还是评论
        String toUid = MapUtils.getString(param, "toUid");
        if (StringUtils.isNotEmpty(toUid)) {
            //回复评论
            saveReplyComment(param, commentUser);
            return resultMap;
        }
        //评论
        TbCommentInfo tbCommentInfo = new TbCommentInfo(param);
        tbCommentInfo.setNickName(StringUtils.isEmpty(commentUser.getNickname()) == true ? commentUser.getPhoneNo() : commentUser.getNikeName());
        tbCommentInfo.setInCompany(commentUser.getInCompany());
        tbCommentInfo.setImage(commentUser.getImageUrl());
        tbCommentInfo.setPost(commentUser.getPosition());
        tbCommentInfo.setNickName(setNikeName(tbCommentInfo.getNickName()));
        tbCommentInfoMapper.insert(tbCommentInfo);
        return resultMap;
    }

    /**
     * 设置回复评论列表
     *
     * @param comments
     * @param param
     */
    private void setReplyComment(List<Map<String, Object>> comments, Map<String, Object> param) {
        if (null != comments && comments.size() > 0) {
            List<Map<String, Object>> replyComments = tbReplyCommentMapper.queryReplyCommtentRelated(param);
            if (null != replyComments && replyComments.size() > 0) {
                List<Map<String, Object>> replys = new ArrayList<>();
                String content = null;
                String replyContent = null;
                for (Map<String, Object> comment : comments) {
                    replys = new ArrayList<>();
                    content = MapUtils.getString(comment, "commContent");
                    if (StringUtils.isNotEmpty(content)) {
                        comment.put("commContent", EmojiParser.parseToUnicode(content));
                    }
                    for (Map<String, Object> reply : replyComments) {
                        replyContent = MapUtils.getString(reply, "replyContent");
                        if (comment.get("pkid").toString().equals(reply.get("commentId").toString())
                                && !Constant.COMMENT_STATES_PINGBI.toString().equals(comment.get("state"))) {
                            if (StringUtils.isNotEmpty(replyContent)) {
                                reply.put("replyContent", EmojiParser.parseToUnicode(replyContent));
                            }
                            replys.add(reply);
                            replyContent = null;
                            continue;
                        }
                    }
                    comment.put("replys", replys);
                    content = null;
                }
                replyComments = null;
            }
        }
    }

    /**
     * 保存回复评论
     *
     * @param param
     * @param commentUser
     */
    public void saveReplyComment(Map<String, Object> param, SysUser commentUser) {
        String toUid = MapUtils.getString(param, "toUid");
        SysUser toUser = userTempBddMapper.queryUserDetailById(toUid);
        String replyContent = MapUtils.getString(param, "content");
        param.put("replyContent", EmojiParser.parseToAliases(replyContent));
        TbReplyComment replyComment = new TbReplyComment(param);
        replyComment.setReCompany(commentUser.getInCompany());
        replyComment.setReImage(commentUser.getImageUrl());
        replyComment.setRePost(commentUser.getPosition());
        replyComment.setReNikeName(setNikeName(StringUtils.isEmpty(commentUser.getNickname()) == true ? commentUser.getPhoneNo() : commentUser.getNikeName()));
        replyComment.setToNikeName(setNikeName(StringUtils.isEmpty(toUser.getNickname()) == true ? toUser.getPhoneNo() : toUser.getNikeName()));
        tbReplyCommentMapper.insert(replyComment);
        if (!replyComment.getToUid().equals(replyComment.getReplyUid())) {
            //推送消息/发送系统内消息
            Map<String, Object> paramters = new HashedMap();
            paramters.put("commentId", replyComment.getCommentId());
            paramters.put("relatedId", replyComment.getRelatedId());
            paramters.put("relatedType", replyComment.getRelatedType());
            paramters.put("replyId",replyComment.getPkid());
            paramters.put("noticeId", messageService.saveReplyCommentMessage(replyComment.getPkid(), replyComment.getToUid()));
            PushMessageUtils.pushMessage(replyComment.getToUid(), paramters);
        }
        replyComment = null;
    }

    /**
     * 设置昵称(如是电话号码需替换*)
     *
     * @param nikeName
     * @return
     */
    private String setNikeName(String nikeName) {
        //判断是否手机号
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(nikeName);
        if (m.matches()) {
            nikeName = tbCompanyService.solPhone(nikeName, "replace");
        }
        return nikeName;
    }
}
