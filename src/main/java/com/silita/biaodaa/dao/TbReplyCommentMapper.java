package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbReplyComment;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * tb_reply_comment Mapper
 */
public interface TbReplyCommentMapper extends MyMapper<TbReplyComment> {

    /**
     * 查询公告/企业所有的回复
     * @param param
     * @return
     */
    List<Map<String,Object>> queryReplyCommtentRelated(Map<String,Object> param);

    /**
     * 添加回复
     * @param tbReplyComment
     * @return
     */
    int insert(TbReplyComment tbReplyComment);
}