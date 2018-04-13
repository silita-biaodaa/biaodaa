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
}