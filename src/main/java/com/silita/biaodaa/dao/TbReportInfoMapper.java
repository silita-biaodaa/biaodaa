package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbReportInfo;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

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

    /**
     * 查询已支付的报告历史记录
     * @return
     */
    List<Map<String,Object>> queryReportList(Map<String,Object> param);

    /**
     * 修改报告订单
     * @param reportInfo
     * @return
     */
    int updateReportOrder(TbReportInfo  reportInfo);

    /**
     * 修改报告订单状态
     * @param reportInfo
     * @return
     */
    int updateReportOrderPayStatus(TbReportInfo  reportInfo);
}