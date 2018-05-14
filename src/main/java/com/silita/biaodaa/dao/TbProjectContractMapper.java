package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectContract;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

/**
 * 合同备案
 * created by zhushuai
 */
public interface TbProjectContractMapper extends MyMapper<TbProjectContract> {

    /**
     * 获取项目合同备案情况
     * @param proId
     * @return
     */
    List<TbProjectContract> queryProjectContractListByProId(Integer proId);
}