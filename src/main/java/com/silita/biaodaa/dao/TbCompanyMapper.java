package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface TbCompanyMapper extends MyMapper<TbCompany> {

    List<TbCompany> queryCompanyList(String keyWord);
}