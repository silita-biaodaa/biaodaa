package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbBidCompute;
import com.silita.biaodaa.utils.MyMapper;

public interface TbBidComputeMapper extends MyMapper<TbBidCompute> {

    /**
     * 保存
     * @param bid
     * @return
     */
    int insertBidCompute(TbBidCompute bid);

}