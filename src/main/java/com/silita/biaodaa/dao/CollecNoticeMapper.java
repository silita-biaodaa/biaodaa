package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.CollecNotice;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * collec_notice Mapper
 */
public interface CollecNoticeMapper extends MyMapper<CollecNotice> {

    /**
     * 获取关注公告详细信息
     *
     * @param collecNotice
     * @return
     */
    CollecNotice getCollecNoticeByUserIdAndNoticeId(CollecNotice collecNotice);

    /**添加
     * @param collecNotice
     */
    void insertCollecNotice(CollecNotice collecNotice);

    /**
     * 删除
     * @param collecNotice
     */
    void deleteCollecNoticeByUserIdAndNoticeId(CollecNotice collecNotice);

    /**
     * 获取招标关注公告
     * @return
     */
    List<Map<String, Object>> listZhaoBiaoCollecNoticeByUserId(Map<String, Object> params);

    /**
     * 获取中标关注公告
     * @param params
     * @return
     */
    List<Map<String, Object>> listZhongBiaoCollecNoticeByUserId(Map<String, Object> params);
}