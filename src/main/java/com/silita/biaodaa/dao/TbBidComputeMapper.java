package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbBidCompute;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbBidComputeMapper extends MyMapper<TbBidCompute> {

    /**
     * 保存
     * @param bid
     * @return
     */
    int insertBidCompute(TbBidCompute bid);

    /**
     * 获取评标历史记录
     * @param param
     * @return
     */
    List<TbBidCompute> queryBidComputeList(Map<String,Object> param);

    /**
     * 删除
     * @param param
     * @return
     */
    int delBidComput(Map<String,Object> param);

    /**
     * 获取项目类型
     * @param pkid
     * @return
     */
    String queryProType(Integer pkid);
}