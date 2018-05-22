package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.ClickRecord;
import com.silita.biaodaa.utils.MyMapper;

/**
 * 点击次数统计（暂时只统计公告点击数）
 * Created by 91567 on 2018/5/22.
 */
public interface ClickRecordMapper extends MyMapper<ClickRecord> {

    /**
     *
     * @return
     */
    Integer getTotalByTypeAndAimIdAndDate(ClickRecord clickRecord);

    /**
     *
     */
    void updateClickRecordByTypeAndAimIdAndDate(ClickRecord clickRecord);

    /**
     *
     */
    void insertClickRecord(ClickRecord clickRecord);

    /**
     *
     */
    Integer getClickCountByTypeAndNoticeId(ClickRecord clickRecord);
}
