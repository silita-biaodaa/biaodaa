package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.VipInfo;
import com.silita.biaodaa.utils.MyMapper;

import java.util.Map;

public interface VipInfoMapper extends MyMapper<VipInfo> {
    VipInfo queryInfoById(Map args);

}
