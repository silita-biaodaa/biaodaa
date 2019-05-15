package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectZhaotoubiao;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * tb_project_zhaotoubiao Mapper
 */
public interface TbProjectZhaotoubiaoMapper extends MyMapper<TbProjectZhaotoubiao> {

    /**
     * 获取项目下招投标列表
     *
     * @param proId
     * @return
     */
    List<TbProjectZhaotoubiao> queryZhaotoubiaoListByProId(String proId);

    /**
     * 获取招投标列表详情
     *
     * @param params
     * @return
     */
    TbProjectZhaotoubiao queryZhaobiaoDetailByProId(Map<String, Object> params);

    /**
     * 获取招投标详情从施工表
     *
     * @param param
     * @return
     */
    Map<String, Object> queryZhaobiaoDetailByBuild(Map<String, Object> param);

    /**
     * 获取详情
     *
     * @param pkid
     * @return
     */
    TbProjectZhaotoubiao queryZhaotouDetailByPkid(String pkid);

    /**
     * 查询招投标个数
     * @param param
     * @return
     */
    int queryProjectZhaotoubiaoCount(Map<String,Object> param);
}