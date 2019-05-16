package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPersonQualification;

import java.util.List;
import java.util.Map;

/**
 * tb_person Mapper
 */
public interface TbPersonMapper {

    /**
     * 获得人员详细信息
     *
     * @param params
     * @return
     */
    Map<String, Object> queryPersonDetail(Map<String, Object> params);

    /**
     * 获取人员详情
     *
     * @param param
     * @return
     */
    List<TbPersonQualification> queryPersonDetailByParam(Map<String, Object> param);

    /**
     * 根据innerid查询人员性别
     * @param innerId
     * @return
     */
    String queryPersonSexInnerId(String innerId);
}