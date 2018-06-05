package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPersonProject;
import com.silita.biaodaa.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

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
     * @param list
     * @return
     */
    List<Map<String,Object>> queryPersonByInnerId(@Param("list") List list,@Param("tabCode") String tabCode);

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

    /**
     * 获取人员信息
     * @param param
     * @return
     */
    List<TbPersonProject> queryPersonByCode(Map<String,Object> param);

    /**
     * 获取人员业绩
     * @param param
     * @return
     */
    List<TbPersonProject> queryPersonProjectByParam(Map<String,Object> param);

    /**
     * 获取人员业绩根据innerid
     * @param innerId
     * @return
     */
    List<TbPersonProject> queryPersonProjectByInnerid(String innerId);
}