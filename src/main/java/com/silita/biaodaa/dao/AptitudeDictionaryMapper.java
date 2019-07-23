package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.AptitudeDictionary;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * 资质
 */
public interface AptitudeDictionaryMapper extends MyMapper<AptitudeDictionary> {

    /**
     * 获取所有资质字典信息
     * created by zhushuai
     *
     * @return
     */
    List<AptitudeDictionary> queryAptitude();

    /**
     * 获取筛选条件
     * created by zhushuai
     *
     * @return
     */
    List<Map<String, String>> getIndustry();

    /**
     * 查询详情
     * @param majorUUid
     * @return
     */
    AptitudeDictionary queryQualDetail(String majorUUid);

    /**
     * 根据code查询资质名称
     * @param qualCode
     * @return
     */
    String queryQualNameByCode(String qualCode);

    /**
     * 根据code查询等级名称
     * @param gradeCode
     * @return
     */
    String queryGradeNameByCode(String gradeCode);
}