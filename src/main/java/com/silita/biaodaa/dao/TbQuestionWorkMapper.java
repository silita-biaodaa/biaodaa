package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbQuestionWork;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbQuestionWorkMapper extends MyMapper<TbQuestionWork> {

    /**
     * 查询非案例题
     * @param param
     * @return
     */
    List<Map<String,Object>> queryWorkQuestionList(Map<String,Object> param);

    /**
     * 查询案例题
     * @param param
     * @return
     */
    List<Map<String,Object>> queryWorkCaseQuestionList(Map<String,Object> param);

    /**
     * 添加
     * @param questionWork
     * @return
     */
    int insertQuestionWork(TbQuestionWork questionWork);

    /**
     * 删除
     * @param questionWork
     * @return
     */
    int deleteQuestionWork(TbQuestionWork questionWork);

    /**
     * 查询是否存在
     * @param questionWork
     * @return
     */
    int queryQuestionWorkExist(TbQuestionWork questionWork);
}