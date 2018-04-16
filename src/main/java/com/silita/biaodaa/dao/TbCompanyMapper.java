package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbCompanyMapper extends MyMapper<TbCompany> {

    List<TbCompany> queryCompanyList(String keyWord);

    TbCompany getCompany(Integer comId);

    List<TbCompany> filterCompany(Map<String,Object> param);

    String getAreaCode(String name);

    List<Map<String,String>> queryProvinceList();

    List<String> queryCityList(String parentId);

    TbCompany getCompanyOrgCode(Integer comId);

    List<String> getCertSrcUuid(String companyCode);

    Map<String,String> getCertAqrz(Map<String,Object> param);

    List<Map<String,Object>> getCertQyhj(Map<String,Object> param);

    List<Map<String,Object>> getUndesirable(Map<String,Object> param);

}