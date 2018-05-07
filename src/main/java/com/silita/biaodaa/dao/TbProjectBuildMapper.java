package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectBuild;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

/**
 *施工许可
 * created by
 */
public interface TbProjectBuildMapper extends MyMapper<TbProjectBuild> {

    List<TbProjectBuild> queryConstartProBuildByProId(Integer proId);
}