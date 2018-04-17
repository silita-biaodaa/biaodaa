package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.CollecNotice;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface CollecNoticeMapper extends MyMapper<CollecNotice> {
    /**
     *
     * @param collecNotice
     * @return
     */
    CollecNotice getCollecNoticeByUserIdAndNoticeId(CollecNotice collecNotice);

    /**
     *
     * @param collecNotice
     */
    void insertCollecNotice(CollecNotice collecNotice);

    /**
     *
     * @param collecNotice
     */
    void deleteCollecNoticeByUserIdAndNoticeId(CollecNotice collecNotice);

    /**
     *
     * @return
     */
    List<CollecNotice> listCollecNoticeByUserIdAndType(Map<String, Object> params);
}