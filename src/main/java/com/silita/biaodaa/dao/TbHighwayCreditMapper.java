package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbHighwayCredit;

import java.util.List;
import java.util.Map;

public interface TbHighwayCreditMapper {

    int insert(TbHighwayCredit credit);

    /**
     * 查询企业的公路信用等级
     *
     * @param param
     * @return
     */
    List<TbHighwayCredit> queryListCompanyHighway(Map<String, Object> param);
}