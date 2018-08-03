package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbClickStatistics;
import com.silita.biaodaa.utils.MyMapper;

/**
 * tb_click_statistics Mapper
 */
public interface TbClickStatisticsMapper extends MyMapper<TbClickStatistics> {

    /**
     * 获取公告点击数
     *
     * @param tbClickStatistics
     * @return
     */
    Integer getTotalBySourceAndTypeAndInnertId(TbClickStatistics tbClickStatistics);

    /**
     * 修改
     *
     * @param tbClickStatistics
     */
    void updateClickStatisticsBySourceAndTypeAndInnertId(TbClickStatistics tbClickStatistics);

    /**
     * 添加
     *
     * @param tbClickStatistics
     */
    void insertClickStatistics(TbClickStatistics tbClickStatistics);

    /**
     * 获取点击数
     *
     * @param tbClickStatistics
     * @return
     */
    Integer getClickCountByBySourceAndTypeAndInnertId(TbClickStatistics tbClickStatistics);
}