package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyInfo;
import com.silita.biaodaa.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbCompanyInfoMapper extends MyMapper<TbCompanyInfo> {

    int batchInsertCompanyInfo(List<TbCompanyInfo> list);

    int insertCompanyInfo(TbCompanyInfo list);

    String queryPhoneByComName(String name);

    TbCompanyInfo queryDetailByComName(String name);

    Integer queryComCount();

    List<TbCompany> queryCompanyList(@Param("page") Integer page, @Param("pageSize") Integer pageSize);

    int updateCompany(TbCompany company);
}