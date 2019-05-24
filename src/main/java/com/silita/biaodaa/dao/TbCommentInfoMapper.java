package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCommentInfo;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * tb_comment_info Mapper
 */
public interface TbCommentInfoMapper extends MyMapper<TbCommentInfo> {

    /**
     * 查询所有父级评论
     * @param param
     * @return
     */
    List<Map<String,Object>> queryCommentList(Map<String,Object> param);

    /**
     * 查询单条父级评论根据主键id
     * @param param
     * @return
     */
    List<Map<String,Object>> querySingleCommentList(Map<String,Object> param);

    /**
     * 查询与我相关父级评论
     * @param param
     * @return
     */
    List<Map<String,Object>> queryUserCommentList(Map<String,Object> param);

    /**
     * 添加评论
     * @param commentInfo
     * @return
     */
    int insert(TbCommentInfo commentInfo);
}