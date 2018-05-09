package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectSupervision;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

/**
 * 监理
 * created by zhushuai
 */
public interface TbProjectSupervisionMapper extends MyMapper<TbProjectSupervision> {

    /**
     * 获取项目下的监理项目
     * @param proId
     * @return
     */
    List<TbProjectSupervision> queryProjectSupervisionListByProId(Integer proId);

}