package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbBidDetail;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface TbBidDetailMapper extends MyMapper<TbBidDetail> {

    /**
     * 批量保存
     * @param list
     * @return
     */
    int batchInsertBidDetail(List<TbBidDetail> list);

}