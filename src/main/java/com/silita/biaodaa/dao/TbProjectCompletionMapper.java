package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectCompletion;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbProjectCompletionMapper extends MyMapper<TbProjectCompletion> {

    /**
     * 获取竣工备案列表
     * @param param
     * @return
     */
    List<TbProjectCompletion> queryProCompleList(Map<String,Object> param);

    /**
     * 获取竣工备案详情
     * @param param
     * @return
     */
    TbProjectCompletion queryProCompleDetail(Map<String,Object> param);

    /**
     * 查询竣工备案个数
     * @param param
     * @return
     */
    int queryProjectCompletionCount(Map<String,Object> param);
}