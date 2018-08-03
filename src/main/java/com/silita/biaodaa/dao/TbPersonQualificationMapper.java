package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPersonQualification;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * tb_person_qualification Mapper
 */
public interface TbPersonQualificationMapper extends MyMapper<TbPersonQualification> {

    /**
     * 获取公司人员资质
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> getCompanyPersonCate(Map<String, Object> param);

    /**
     * 获取公司人员
     *
     * @param param
     * @return
     */
    List<TbPersonQualification> queryCompanyPerson(Map<String, Object> param);

    /**
     * 获取省份
     *
     * @return
     */
    List<Map<String, Object>> getProvinceList();

    /**
     * 获取身份code
     *
     * @param name
     * @return
     */
    String getProvinceCode(String name);

    /**
     * 修改
     *
     * @param param
     */
    void updatePersonPX(Map<String, Object> param);
}