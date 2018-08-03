package com.silita.biaodaa.dao;

import java.util.List;
import java.util.Map;

/**
 * 评标办法
 */
public interface BidEvaluationMethodMapper {

    /**
     * 获取所有获奖的最新年份
     *
     * @return
     */
    List<Map<String, Object>> queryCertYears();

    /**
     * 获取奖项
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> queryCertPrizeList(Map<String, Object> param);

    /**
     * 获取企业安全认证
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> quaryCertAqrz(Map<String, Object> param);

    /**
     * 获取企业信用等级等级
     *
     * @param param
     * @return
     */
    Map<String, Object> queryCertGrade(Map<String, Object> param);

    /**
     * 获取企业信用等级最新年份
     *
     * @return
     */
    String queryGradeYear();

    /**
     * 获取奖项个数
     *
     * @param param
     * @return
     */
    Integer queryCertPrizeCount(Map<String, Object> param);
}
