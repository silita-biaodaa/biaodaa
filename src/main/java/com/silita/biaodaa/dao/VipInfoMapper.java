package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.VipInfo;
import com.silita.biaodaa.model.TbVipFeeStandard;

import java.util.List;
import java.util.Map;

public interface VipInfoMapper {
    VipInfo queryInfoById(Map args);

    List<TbVipFeeStandard>  queryFeeStandard(String channel);
}
