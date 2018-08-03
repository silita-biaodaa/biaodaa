package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCountBidInfo;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * tb_count_bid_info Mapper
 */
public interface TbCountBidInfoMapper extends MyMapper<TbCountBidInfo> {

    /**
     * 获取单月的中标第一候选人统计
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> queryZhongbiaoCount(Map<String, Object> param);

    /**
     * 删除单月记录
     *
     * @param statDate
     * @return
     */
    int delZhongBiaoCountByDate(String statDate);

    /**
     * 批量添加
     *
     * @param list
     * @return
     */
    int batchInsertCountBinInfo(List<Map<String, Object>> list);

    /**
     * 添加
     *
     * @param param
     * @return
     */
    int insertCountBinInfo(Map<String, Object> param);

    /**
     * 获取详情列表
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> queryCountBidList(Map<String, Object> map);

    /**
     * 获取个数
     *
     * @param map
     * @return
     */
    Map<String, Object> queryCountBidNum(Map<String, Object> map);

    /**
     * 获取当月统计数
     *
     * @param statDate
     * @return
     */
    int queryCountBidByStat(String statDate);
}