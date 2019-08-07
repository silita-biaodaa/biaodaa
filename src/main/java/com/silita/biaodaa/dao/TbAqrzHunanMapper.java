package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbAqrzHunan;
import com.silita.biaodaa.utils.MyMapper;

import java.util.Map;

public interface TbAqrzHunanMapper extends MyMapper<TbAqrzHunan> {

    /**
     * 查询安全认证
     * @param param
     * @return
     */
    Map<String,Object> queryAqrz(Map<String,Object> param);

    /**
     * 查询企业的安全认证
     * @param param
     * @return
     */
    Map<String,Object> queryCompanyAqrz(Map<String,Object> param);
}