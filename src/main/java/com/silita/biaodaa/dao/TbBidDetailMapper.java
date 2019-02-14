package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbBidDetail;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * tb_bid_detail Mapper
 */
public interface TbBidDetailMapper extends MyMapper<TbBidDetail> {

    /**
     * 批量保存
     *
     * @param list
     * @return
     */
    int batchInsertBidDetail(List<TbBidDetail> list);

    /**
     * 获奖详情
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> queryPrizeBidDetail(Map<String, Object> param);

    /**
     * 获取公司获奖情况
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> queryMateNameList(Map<String, Object> param);

    /**
     * 获取安全认证
     *
     * @param param
     * @return
     */
    String querySafetyDetail(Map<String, Object> param);

    /**
     * 获取不良行为
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> queryUnabDetail(Map<String, Object> param);
}