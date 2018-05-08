package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProject;
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
     * 根据区域名称和Path
     * @param
     * @return
     */
    List<Map<String,Object>> queryNameAndPath();

    /**
     * 获取项目详情
     * @param proId 项目Id
     * @return
     */
    TbProject queryProjectDetail(Integer proId);


}
