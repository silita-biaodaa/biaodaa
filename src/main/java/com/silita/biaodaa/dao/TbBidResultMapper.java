package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbBidResult;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbBidResultMapper extends MyMapper<TbBidResult> {

    /**
     * 保存
     * @param bidResult
     * @return
     */
    int insertBidResult(TbBidResult bidResult);

    /**
     * 查询列表
     * @param param
     * @return
     */
    List<TbBidResult> queryBidResultList(Map<String,Object> param);
}