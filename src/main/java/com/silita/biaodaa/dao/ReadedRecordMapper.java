package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.ReadedRecord;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface ReadedRecordMapper extends MyMapper<ReadedRecord> {
    /**
     *
     * @param params
     * @return
     */
    Integer getTotalByMsgId(Map<String, Object> params);

    /**
     *
     * @param readedRecord
     */
    void insertReadedRecord(ReadedRecord readedRecord);

    /**
     *
     * @param
     */
    void batchUpdateReadOrReadedByUserIds(List<Map<String, Object>> ids);


    /**
     *
     * @param params
     */
    void updateReadOrReadedById(Map<String, Object> params);

}