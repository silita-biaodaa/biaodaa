package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompanyReport;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * tb_company_report (企业年报)
 */
public interface TbCompanyReportMapper extends MyMapper<TbCompanyReport> {

    /**
     * 添加企业年报
     * @param companyReport
     * @return
     */
    int insertCompanyReport(TbCompanyReport companyReport);

    /**
     * 查询年报是否存在
     * @param companyReport
     * @return
     */
    int queryCompanyCount(TbCompanyReport companyReport);

    /**
     * 修改企业年报
     * @param companyReport
     * @return
     */
    int updateCompanyReport(TbCompanyReport companyReport);

    /**
     * 查询公司的年报年份
     * @param param
     * @return
     */
    List<Map<String,Object>> queryReportYearsForCompany(Map<String,Object> param);

    /**
     * 年报详情
     * @param param
     * @return
     */
    String queryReportDetailForCompany(Map<String,Object> param);
}