package com.silita.biaodaa.dao;

import com.silita.biaodaa.utils.MyMapper;

import java.rmi.MarshalledObject;
import java.util.List;
import java.util.Map;

public interface TbProjectMapper extends MyMapper {

    /**
     * 按工程查询业绩列表
     * created by zhushuai
     * @param params
     * @return
     */
    List<Map<String,Object>> queryObject(Map<String,Object> params);

    /**
     * 按单位查询业绩列表
     * created by zhushuai
     * @param params
     * @return
     */
    List<Map<String,Object>> queryObjectByUnit(Map<String,Object> params);

    /**
     * 根据path查询区域名称
     * @param path
     * @return
     */
    List<Map<String,Object>> queryNameByPath();
}
