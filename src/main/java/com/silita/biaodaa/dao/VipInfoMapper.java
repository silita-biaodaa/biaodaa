package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbVipFeeStandard;
import com.silita.biaodaa.model.TbVipInfo;
import com.silita.biaodaa.model.TbVipProfits;

import java.util.List;

public interface VipInfoMapper {
    TbVipInfo queryVipInfoById(String userId);

    int updateVipInfo(TbVipInfo tbVipInfo);

    int insertVipInfo(TbVipInfo tbVipInfo);

    List<TbVipFeeStandard>  queryFeeStandard(String channel);

    List<TbVipProfits>  queryProfitInfo(String userId);

    Integer queryProfitTotal(String userId);

    /**
     * 查询收费标准对象
     * @param stdCode
     * @return
     */
    TbVipFeeStandard queryFeeStandardByCode(String stdCode);

}
