package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompanyInfo;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface TbCompanyInfoMapper extends MyMapper<TbCompanyInfo> {

    int batchInsertCompanyInfo(List<TbCompanyInfo> list);

    int insertCompanyInfo(TbCompanyInfo list);

    String queryPhoneByComName(String name);

    TbCompanyInfo queryDetailByComName(String name);
}