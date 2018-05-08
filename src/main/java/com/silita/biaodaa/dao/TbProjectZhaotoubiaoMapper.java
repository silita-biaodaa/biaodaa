package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectZhaotoubiao;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface TbProjectZhaotoubiaoMapper extends MyMapper<TbProjectZhaotoubiao> {

    /**
     * 获取项目下招投标列表
     * @param proId
     * @return
     */
    List<TbProjectZhaotoubiao> queryZhaotoubiaoListByProId(Integer proId);
}