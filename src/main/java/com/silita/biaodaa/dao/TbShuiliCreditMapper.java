package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbShuiliCredit;

import java.util.List;
import java.util.Map;

public interface TbShuiliCreditMapper{

    int insert(TbShuiliCredit credit);

    /**
     * 查询公司水利信用等级
     * @param param
     * @return
     */
    List<TbShuiliCredit> queryListCompanyShuiliCredit(Map<String,Object> param);
}