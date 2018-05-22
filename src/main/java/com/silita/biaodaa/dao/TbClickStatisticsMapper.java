package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbClickStatistics;
import com.silita.biaodaa.utils.MyMapper;

public interface TbClickStatisticsMapper extends MyMapper<TbClickStatistics> {

    /**
     *
     * @param tbClickStatistics
     * @return
     */
    Integer getTotalBySourceAndTypeAndInnertId(TbClickStatistics tbClickStatistics);

    /**
     *
     * @param tbClickStatistics
     */
    void updateClickStatisticsBySourceAndTypeAndInnertId(TbClickStatistics tbClickStatistics);

    /**
     *
     * @param tbClickStatistics
     */
    void insertClickStatistics(TbClickStatistics tbClickStatistics);

    /**
     *
     * @param tbClickStatistics
     * @return
     */
    Integer getClickCountByBySourceAndTypeAndInnertId(TbClickStatistics tbClickStatistics);


}