package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbAwardHunan;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbAwardHunanMapper extends MyMapper<TbAwardHunan> {

    /**
     * 获取奖项的最新年份
     * @param awardName
     * @return
     */
    String queryYears(String awardName);

    /**
     * 查询奖项
     * @param param
     * @return
     */
    List<Map<String,Object>> queryAwardsList(Map<String,Object> param);

    /**
     * 查询国家级获奖
     * @param param
     * @return
     */
    List<Map<String,Object>> queryGjhjAwardsList(Map<String,Object> param);

    /**
     * 查询省级级获奖
     * @param param
     * @return
     */
    List<Map<String,Object>> querySjhjAwardsList(Map<String,Object> param);

    /**
     * 查询企业获奖
     * @param param
     * @return
     */
    List<Map<String,Object>> queryCompanyAwards(Map<String,Object> param);

    /**
     * 查询公司奖项名称
     * @param param
     * @return
     */
    List<Map<String,Object>> queryCompanyAwardsGroup(Map<String,Object> param);
}