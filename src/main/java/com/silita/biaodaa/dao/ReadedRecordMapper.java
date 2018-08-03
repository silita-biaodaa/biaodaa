package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.ReadedRecord;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * readed_record Mapper
 */
public interface ReadedRecordMapper extends MyMapper<ReadedRecord> {
    /**
     * 获取读取记录
     *
     * @param params
     * @return
     */
    Integer getTotalByMsgId(Map<String, Object> params);

    /**
     * 添加
     *
     * @param readedRecord
     */
    void insertReadedRecord(ReadedRecord readedRecord);

    /**
     * 批量添加
     *
     * @param
     */
    void batchUpdateReadOrReadedByUserIds(List<Map<String, Object>> ids);


    /**
     * 修改
     *
     * @param params
     */
    void updateReadOrReadedById(Map<String, Object> params);

}