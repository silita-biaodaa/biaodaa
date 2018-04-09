package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPersonQualification;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbPersonQualificationMapper extends MyMapper<TbPersonQualification> {

    List<Map<String,Object>> getCompanyPersonCate(Integer comId);

    List<TbPersonQualification> queryCompanyPerson(Map<String,Object> param);
}