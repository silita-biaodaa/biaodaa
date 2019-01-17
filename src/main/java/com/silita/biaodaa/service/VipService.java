package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.VipInfoMapper;
import com.silita.biaodaa.model.TbVipFeeStandard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by dh on 2019/1/4.
 */
@Service
public class VipService {
    @Autowired
    private VipInfoMapper vipInfoMapper;

    public List<TbVipFeeStandard> queryFeeStandard(String channel){
        return vipInfoMapper.queryFeeStandard(channel);
    }
}
