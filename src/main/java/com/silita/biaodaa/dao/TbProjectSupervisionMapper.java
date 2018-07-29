package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectSupervision;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * 监理
 * created by zhushuai
 */
public interface TbProjectSupervisionMapper extends MyMapper<TbProjectSupervision> {

    /**
     * 获取项目下的监理项目
     * created by zhushuai
     * @param proId
     * @return
     */
    List<TbProjectSupervision> queryProjectSupervisionListByProId(String proId);

    /**
     * 获取监理单位
     * created by zhushuai
     * @param proId
     * @return
     */
    List<Map<String,Object>> queryProjectSupCompany(String proId);

    /**
     * 获取监理下的主项目Id
     * @param comId
     * @return
     */
    List<String> queryProIdByComId(String comId);

    /**
     * 获取中标下的监理
     * @param param
     * @return
     */
    Map<String,Object> querySuperDetail(Map<String,Object> param);

    /**
     * 获取监理详情
     * @param pkid
     * @return
     */
    TbProjectSupervision querySupervisionDetailById(String pkid);
}