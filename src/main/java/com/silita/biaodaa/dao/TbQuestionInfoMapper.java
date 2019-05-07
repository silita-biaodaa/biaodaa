package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbQuestionInfo;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbQuestionInfoMapper extends MyMapper<TbQuestionInfo> {

    /**
     * 查询题目类别
     * @param param
     * @return
     */
    List<Map<String,Object>> queryQuestionType(Map<String,Object> param);

    /**
     * 题目个数
     * @param param
     * @return
     */
    int queryQuestionCount(Map<String,Object> param);

    /**
     * 案例题目个数(包括简答)
     * @param param
     * @return
     */
    int queryQuestionCaseCount(Map<String,Object> param);

    /**
     * 查询题目列表
     * @param param
     * @return
     */
    List<Map<String,Object>> queryQuestionList(Map<String,Object> param);

    /**
     * 案例题目列表
     * @param param
     * @return
     */
    List<Map<String,Object>> queryCaseQuestionList(Map<String,Object> param);
}