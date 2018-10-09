package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompanyQualification;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

/**
 * tb_company_qualification Mapper
 */
public interface TbCompanyQualificationMapper extends MyMapper<TbCompanyQualification> {

    /**
     * 获取企业资质
     *
     * @param comId
     * @return
     */
    List<TbCompanyQualification> queryCompanyQualification(String comId);

    /**
     * 查询公司资质
     * @param comId
     * @return
     */
    Integer queryCompanyQualCount(String comId);
}