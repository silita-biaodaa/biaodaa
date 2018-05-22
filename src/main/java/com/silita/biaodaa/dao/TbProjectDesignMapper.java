package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectDesign;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * 施工图审查Mapper
 * created by zhushuai
 */
public interface TbProjectDesignMapper extends MyMapper<TbProjectDesign> {

    /**
     * 根据项目id获取施工图审查
     * created by zhushuai
     * @param id
     * @return
     */
    List<TbProjectDesign> queryProjectDesignByProId(Integer id);

    /**
     * 获取详情
     * created by zhushuai
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
     * created by zhushuai
     * @param proId
     * @return
     */
    TbProjectDesign queryProjectDesignDetailLimitOneByProId(Integer proId);

    /**
     * 根据主键id获取详情
     * created by zhushuai
     * @param pkid
     * @return
     */
    TbProjectDesign queryProjectDesignDetailByPkid(Integer pkid);

    /**
     * 获取勘察公司名称
     * created by zhushuai
     * @param proId
     * @return
     */
    List<Map<String,Object>> queryExProjectDesignCompany(Integer proId);

    /**
     * 获取设计公司名称
     * created by zhushuai
     * @param proId
     * @return
     */
    List<Map<String,Object>> queryDeProjectDesignCompany(Integer proId);

    /**
     * 获取勘察设计下的主项目Id
     * @param comId
     * @return
     */
    List<Integer> queryProIdByComId(Integer comId);
}