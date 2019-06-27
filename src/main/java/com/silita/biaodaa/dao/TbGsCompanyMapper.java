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

}