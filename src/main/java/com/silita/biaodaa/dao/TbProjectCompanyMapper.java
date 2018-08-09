package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectCompany;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * tb_project_company Mapper
 */
public interface TbProjectCompanyMapper extends MyMapper<TbProjectCompany> {

    /**
     * 获取企业业绩
     *
     * @param param
     * @return
     */
    List<String> queryProIdForCom(Map<String, Object> param);

    /**
     * 获取对应的业绩企业信息
     * @param param
     * @return
     */
    List<TbProjectCompany> queryProComList(Map<String,Object> param);

}