package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbReportInfo;
import com.silita.biaodaa.utils.MyMapper;

/**
 * tb_report_info
 */
public interface TbReportInfoMapper extends MyMapper<TbReportInfo> {

    /**
     * 添加
     * @param reportInfo
     * @return
     */
    int insert(TbReportInfo reportInfo);
}