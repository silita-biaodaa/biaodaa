package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompanyQualification;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface TbCompanyQualificationMapper extends MyMapper<TbCompanyQualification> {

    List<TbCompanyQualification> queryCompanyQualification(String comId);
}