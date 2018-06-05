package com.silita.biaodaa.dao;

import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * 字典表
 * created by zhushuai
 */
public interface TwfDictMapper extends MyMapper<Map<String,Object>> {

    /**
     * 获取全部省份
     * @return
     */
    List<Map<String,Object>> getProvinceList();

    /**
     * 根据省份名称获取code
     * @param name
     * @return
     */
    String getProvinceCode(String name);

}