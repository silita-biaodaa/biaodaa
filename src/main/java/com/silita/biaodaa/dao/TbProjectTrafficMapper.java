package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectTraffic;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbProjectTrafficMapper extends MyMapper<TbProjectTraffic> {

    /**
     * 查询公路业绩
     * @param param
     * @return
     */
    List<Map<String,Object>> queryProjectList(Map<String,Object> param);

    /**
     * 查询详情
     * @param pkid
     * @return
     */
    TbProjectTraffic queryProjectDetail(String pkid);
}