package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbGsCompany;
import com.silita.biaodaa.utils.MyMapper;

import java.util.Map;

public interface TbGsCompanyMapper extends MyMapper<TbGsCompany> {

    /**
     * 查询是否在一周内更新
     * @param param
     * @return
     */
    int queryGsCompanyCount(Map<String,Object> param);

    /**
     * 添加
     * @param tbGsCompany
     * @return
     */
    int insertGsCompany(TbGsCompany tbGsCompany);

    /**
     * 修改
     * @param tbGsCompany
     * @return
     */
    int updatedGsCompany(TbGsCompany tbGsCompany);

    /**
     * 查询企业是否存在
     * @param comId
     * @return
     */
    int queryCompanyExits(String comId);

    /**
     * 查询企业单个字段
     * @param param
     * @return
     */
    Map<String,Object> queryCompanyParamter(Map<String,Object> param);
}