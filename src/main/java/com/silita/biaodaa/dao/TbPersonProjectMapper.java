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

    /**
     * 根据innerid查询人员详细信息
     * @param innerId
     * @return
     */
    Map<String,Object> queryPersonByInnerId(String innerId);

    /**
     * 根据Pkid获取innerId
     * @param list
     * @return
     */
    List<TbPersonProject> queryInnerIdByPkid(List list);

    /**
     * 根据id获取人员信息
     * @param param
     * @return
     */
    List<TbPersonProject> queryPersonProjectByPid(Map<String,Object> param);

    /**
     * 根据类型获取人员信息
     * @param param
     * @return
     */
    List<TbPersonProject> queryPersonByParam(Map<String,Object> param);
}