package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbReviewDiff;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbReviewDiffMapper extends MyMapper<TbReviewDiff> {

    /**
     * 查询考评不合格情况
     * @param param
     * @return
     */
    List<Map<String,Object>> queryReviewDiff(Map<String,Object> param);

    /**
     * 查询考评不合格情况(数量)
     * @param param
     * @return
     */
    int queryReviewDiffCount(Map<String,Object> param);
}