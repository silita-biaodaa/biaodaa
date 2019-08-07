package com.silita.biaodaa.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by zhushuai on 2019/3/5.
 */
public interface PrizeMapper {

    /**
     * 查询获奖条件
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> queryPrizeFilter(Map<String, Object> param);

    /**
     * 查询获奖列表
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> queryPrizeList(Map<String, Object> param);

    /**
     * 查询不良记录列表
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> queryundesirableList(Map<String, Object> param);

    /**
     * 查询不良行为个数
     * @param param
     * @return
     */
    int queryUndersiableCount(Map<String,Object> param);

    /**
     * 查询不良行为
     * @param param
     * @return
     */
    List<Map<String, Object>> queryUndersiableList(Map<String,Object> param);

    /**
     * 查询企业不良行为
     * @param param
     * @return
     */
    List<Map<String, Object>> queryCompanyUndersiableList(Map<String,Object> param);
}
