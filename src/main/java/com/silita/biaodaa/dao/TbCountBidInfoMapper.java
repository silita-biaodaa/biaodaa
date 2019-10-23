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
     * 获取详情列表(根据日期)
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> queryCountBidDateList(Map<String, Object> map);

    /**
     * 获取详情列表(根据关联id)
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> queryCountBidList(Map<String, Object> map);
}