package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbVipFeeStandard;
import com.silita.biaodaa.model.TbVipProfits;
import com.silita.biaodaa.model.VipInfo;

import java.util.List;
import java.util.Map;

public interface VipInfoMapper {
    VipInfo queryInfoById(Map args);

    List<TbVipFeeStandard>  queryFeeStandard(String channel);

    List<TbVipProfits>  queryProfitInfo(String userId);

    Integer queryProfitTotal(String userId);
}
