package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbDownloadRecord;
import com.silita.biaodaa.utils.MyMapper;

import java.util.Map;

/**
 * 下载统计表
 * created by zhushuai
 */
public interface TbDownloadRecordMapper extends MyMapper<TbDownloadRecord> {

    /**
     * 获取文件的下载次数
     * @param param
     * @return
     */
    TbDownloadRecord queryDownloadRecord(Map<String,Object> param);

    /**
     * 新增
     * @param downloadRecord
     * @return
     */
    int insertDownloadRecord(TbDownloadRecord downloadRecord);

    /**
     * 修改
     * @param downloadRecord
     * @return
     */
    int updateDownloadRecord(TbDownloadRecord downloadRecord);
}