package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbAwardNationwide;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * 全国奖项 tb_award_nationwide Mapper
 */
public interface TbAwardNationwideMapper extends MyMapper<TbAwardNationwide> {

    /**
     * 添加
     * @param tbAwardNationwide
     * @return
     */
    int insertAward(TbAwardNationwide tbAwardNationwide);

    /**
     * 添加不存在的单位名称
     * @param comName
     * @return
     */
    int insertNotCompany(String comName);

    /**
     * 查询企业奖项类别
     * @param param
     * @return
     */
    List<Map<String,Object>> queryCompanyAwardsGroup(Map<String,Object> param);

    /**
     * 查询企业获奖
     * @param param
     * @return
     */
    List<Map<String,Object>> queryCompanyAwards(Map<String,Object> param);

    /**
     * 查询奖项详情
     * @param param
     * @return
     */
    Map<String,Object> queryCompanyAwardDetail(Map<String,Object> param);
}