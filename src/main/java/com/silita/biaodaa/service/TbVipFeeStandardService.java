package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbVipFeeStandardMapper;
import com.silita.biaodaa.model.TbVipFeeStandard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TbVipFeeStandardService {
    @Autowired
    private TbVipFeeStandardMapper tbVipFeeStandardMapper;
    /**
     * 按类型获取所有价格
     * @param param
     * @return
     */
    public List<TbVipFeeStandard> getVipFeeStandard(Map<String,Object> param){
        return tbVipFeeStandardMapper.queryVipFeeStandard(param);
    }
}
