package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.dao.TbGsCompanyMapper;
import com.silita.biaodaa.model.TbCompany;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 工商企业
 * Created by zhushuai on 2019/6/28.
 */
@Service
public class CompanyGsService {

    @Autowired
    TbGsCompanyMapper tbGsCompanyMapper;
    @Autowired
    TbCompanyMapper tbCompanyMapper;

    /**
     * 工商基本信息
     * @param company
     * @return
     */
    public Map<String, Object> getGsCompany(TbCompany company) {
        if (tbGsCompanyMapper.queryCompanyExits(company.getComId()) <= 0) {
            return null;
        }
        Map<String, Object> param = new HashedMap() {{
            put("paramter", "basic");
        }};
        param.put("comId", company.getComId());
        Map<String, Object> comMap = tbGsCompanyMapper.queryCompanyParamter(param);
        Map<String, Object> resultMap = JSONObject.parseObject(comMap.get("paramter").toString());
        resultMap.put("updated", comMap.get("updated"));
        resultMap.put("comId",company.getComId());
        resultMap.put("phone",company.getPhone());
        resultMap.put("email",company.getEmail());
        resultMap.put("comUrl",company.getComUrl());
        resultMap.put("collected",company.getCollected());
        resultMap.put("isUpdated",1);
        resultMap.put("certNo",company.getCertNo());
        resultMap.put("validDate",company.getValidDate());
        comMap = null;
        company = null;
        return resultMap;
    }

    /**
     * 查询股东信息、主要人员、变更记录、企业年报、行政处罚
     * @param param
     * @return
     */
    public Object getGsCompangInfo(Map<String,Object> param){

        return null;
    }
}
