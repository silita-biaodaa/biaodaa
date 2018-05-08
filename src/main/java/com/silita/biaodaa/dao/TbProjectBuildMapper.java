package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectBuild;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

/**
 *施工许可
 * created by
 */
public interface TbProjectBuildMapper extends MyMapper<TbProjectBuild> {

    /**
     * 获取项目下的合同备案解析字段
     * created by zhushuai
     * @param proId
     * @return
     */
    List<TbProjectBuild> queryConstartProBuildByProId(Integer proId);

    /**
     * 获取项目下的招投标解析字段
     * @param proId
     * @return
     */
    List<TbProjectBuild> queryZhaobiaoProByProId(Integer proId);

    /**
     * 获取项目下的所有施工许可
     * @param proId
     * @return
     */
    List<TbProjectBuild> queryProjectBuildByProId(Integer proId);
}