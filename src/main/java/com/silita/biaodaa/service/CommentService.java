package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.odianyun.util.sensi.SensitiveFilter;
import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.dao.TbCommentInfoMapper;
import com.silita.biaodaa.dao.TbReplyCommentMapper;
import com.silita.biaodaa.model.Page;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhushuai on 2019/5/22.
 */
@Service
public class CommentService {

    @Autowired
    TbCommentInfoMapper tbCommentInfoMapper;
    @Autowired
    TbReplyCommentMapper tbReplyCommentMapper;

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

    public Map<String, Object> pushComment(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashedMap();
        resultMap.put("code", Constant.SUCCESS_CODE);
        resultMap.put("msg", "操作成功！");
        String content = MapUtils.getString(param, "content");
        //是否包含敏感词汇
        SensitiveFilter filter = SensitiveFilter.DEFAULT;
        String replceStr = filter.filter(content,'*');
        if (!content.equalsIgnoreCase(replceStr)) {
            resultMap.put("code", Constant.ERR_COMMTENT_MINGAN);
            resultMap.put("msg", "包含敏感词汇，请重新输入！");
            return resultMap;
        }
        //判断回复评论还是评论
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
                for (Map<String, Object> comment : comments) {
                    for (Map<String, Object> reply : replyComments) {
                        if (comment.get("userId").toString().equals(reply.get("commentId"))
                                && !Constant.COMMENT_STATES_PINGBI.equals(comment.get("state"))) {
                            replys.add(reply);
                            continue;
                        }
                    }
                    comment.put("replys", replys);
                }
                replyComments = null;
            }
        }
    }

}
