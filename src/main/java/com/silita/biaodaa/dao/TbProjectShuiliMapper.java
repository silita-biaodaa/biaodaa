package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectShuili;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbProjectShuiliMapper extends MyMapper<TbProjectShuili> {

    /**
     * 业绩水利列表
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> queryShuiliProjectList(Map<String, Object> param);

    /**
     * 获取详情
     * @param proId
     * @return
     */
    TbProjectShuili queryShuiliDetail(String proId);

    /**
     * 获取水利项目类型
     * @return
     */
    List<String> queryShuiliProType();
}