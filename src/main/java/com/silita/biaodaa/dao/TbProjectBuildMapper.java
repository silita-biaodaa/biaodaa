package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectBuild;
import com.silita.biaodaa.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.net.Inet4Address;
import java.util.List;
import java.util.Map;

/**
 * 施工许可
 * created by
 */
public interface TbProjectBuildMapper extends MyMapper<TbProjectBuild> {

    /**
     * 获取项目下的合同备案解析字段
     * created by zhushuai
     *
     * @param proId
     * @return
     */
    List<TbProjectBuild> queryConstartProBuildByProId(String proId);

    /**
     * 获取项目下的招投标解析字段
     *
     * @param proId
     * @return
     */
    List<TbProjectBuild> queryZhaobiaoProByProId(String proId);

    /**
     * 获取项目下的所有施工许可
     *
     * @param proId
     * @return
     */
    List<TbProjectBuild> queryProjectBuildByProId(String proId);

    /**
     * 获取施工许可详情
     *
     * @param proId
     * @param pkid
     * @return
     */
    TbProjectBuild queryProjectBuildDetail(@Param("proId") String proId, @Param("pkid") String pkid);

    /**
     * 获取该项目下的施工单位
     *
     * @param proId
     * @return
     */
    List<Map<String, Object>> queryProjectBuildCompany(String proId);

    /**
     * 公司下的主项目Id
     *
     * @param comId
     * @return
     */
    List<String> queryProIdByComId(String comId);
}