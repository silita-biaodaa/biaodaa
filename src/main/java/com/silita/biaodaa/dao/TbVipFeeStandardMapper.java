package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbTrafficPerson;
import com.silita.biaodaa.model.TbVipFeeStandard;
import com.silita.biaodaa.utils.MyMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TbVipFeeStandardMapper extends MyMapper<TbVipFeeStandard> {

    /**
     * 按类型获取所有价格
     * @param param
     * @return
     */
    List<TbVipFeeStandard> queryVipFeeStandard(Map<String,Object> param);

}
