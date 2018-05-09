package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectDesign;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

/**
 * 施工图审查Mapper
 * created by zhushuai
 */
public interface TbProjectDesignMapper extends MyMapper<TbProjectDesign> {

    /**
     * 根据项目id获取施工图审查
     * @param id
     * @return
     */
    List<TbProjectDesign> queryProjectDesignByProId(Integer id);

    /**
     * 获取详情
     * @param proId
     * @return
     */
    List<TbProjectDesign> queryProjectDesignDetailByProId(Integer proId);

    /**
     * 根据市获取省
     * @param name
     * @return
     */
    String queryProvinceByName(String name);

    /**
     * 查询基本信息
     * @param proId
     * @return
     */
    TbProjectDesign queryProjectDesignDetailLimitOneByProId(Integer proId);
}