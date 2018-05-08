package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPersonProject;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbPersonProjectMapper extends MyMapper<TbPersonProject> {

    /**
     * 获取项目人
     * @param param
     * @return
     */
    List<TbPersonProject> queryPersonProject(Map<String,Object> param);
}