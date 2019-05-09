package com.silita.biaodaa.service;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.dao.TbQuestionInfoMapper;
import com.silita.biaodaa.dao.TbQuestionWorkMapper;
import com.silita.biaodaa.model.TbQuestionWork;
import com.silita.biaodaa.utils.ObjectUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhushuai on 2019/5/7.
 */
@Service
public class QuestionService {

    @Autowired
    TbQuestionInfoMapper tbQuestionInfoMapper;
    @Autowired
    TbQuestionWorkMapper tbQuestionWorkMapper;
    @Autowired
    MyRedisTemplate myRedisTemplate;

    /**
     * 题目类别
     *
     * @return
     */
    public List<Map<String, Object>> getQuestionType() {
        List<Map<String, Object>> list = myRedisTemplate.getList("question_type");
        if (null != list && list.size() > 0) {
            return list;
        }
        Map<String, Object> param = new HashedMap() {{
            put("level", 1);
        }};
        List<Map<String, Object>> parentList = tbQuestionInfoMapper.queryQuestionType(param);
        for (Map<String, Object> map : parentList) {
            param.put("level", 2);
            param.put("parentId", MapUtils.getInteger(map, "pkid"));
            map.put("list", tbQuestionInfoMapper.queryQuestionType(param));
        }
        if (null != parentList && parentList.size() > 0) {
            myRedisTemplate.setObject("question_type", parentList);
        }
        return parentList;
    }

    /**
     * 题目个数
     *
     * @param param
     * @return
     */
    public Map<String, Object> getQuestionCount(Map<String, Object> param) {
        String key = "question_count" + ObjectUtils.buildMapParamHash(param);
        Map<String, Object> countMap = (Map<String, Object>) myRedisTemplate.getObject(key);
        if (MapUtils.isNotEmpty(countMap)) {
            return countMap;
        }
        countMap = new HashedMap(4);
        //单选
        param.put("subType", Constant.QUESTION_TYPE_RADIO);
        countMap.put("radio", tbQuestionInfoMapper.queryQuestionCount(param));
        //多选
        param.put("subType", Constant.QUESTION_TYPE_CHECKBOX);
        countMap.put("checkbox", tbQuestionInfoMapper.queryQuestionCount(param));
        //判断
        param.put("subType", Constant.QUESTION_TYPE_JUDGE);
        countMap.put("judge", tbQuestionInfoMapper.queryQuestionCount(param));
        //案例
        countMap.put("case", tbQuestionInfoMapper.queryQuestionCaseCount(param));
        myRedisTemplate.setObject(key, countMap);
        return countMap;
    }

    /**
     * 题目类别
     *
     * @return
     */
    public List<Map<String, Object>> getListQuestion(Map<String, Object> param) {
        String key = "question_list_" + ObjectUtils.buildMapParamHash(param);
        List<Map<String, Object>> list = myRedisTemplate.getList(key);
        if (null != list && list.size() > 0) {
            return list;
        }
        Integer subType = MapUtils.getInteger(param, "subType");
        List<Map<String, Object>> questionList = null;
        if (Constant.QUESTION_TYPE_CASE.intValue() == subType.intValue()) {
            questionList = tbQuestionInfoMapper.queryCaseQuestionList(param);
        } else {
            questionList = tbQuestionInfoMapper.queryQuestionList(param);
        }
        setQuestion(questionList);
        if (null != questionList && questionList.size() > 0) {
            myRedisTemplate.setObject(key, questionList);
        }
        return questionList;
    }

    /**
     * 我的错题/收藏
     *
     * @param param
     * @return
     */
    public List<Map<String, Object>> getListQuestionWork(Map<String, Object> param) {
        List<Map<String, Object>> list = new ArrayList<>();
        //非案例题
        list = tbQuestionWorkMapper.queryWorkQuestionList(param);
        //案例题
        list.addAll(tbQuestionWorkMapper.queryWorkCaseQuestionList(param));
        setQuestion(list);
        return list;
    }

    /**
     * 添加错题/收藏
     *
     * @param param
     */
    public void addQuestionWork(Map<String, Object> param) {
        TbQuestionWork questionWork = new TbQuestionWork();
        questionWork.setUserId(VisitInfoHolder.getUid());
        questionWork.setWorkType(MapUtils.getInteger(param, "workType"));
        questionWork.setSubType(MapUtils.getInteger(param, "subType"));
        questionWork.setQuestionId(MapUtils.getInteger(param, "pkid"));
        questionWork.setQuestionType(MapUtils.getInteger(param,"questionType"));
        if (tbQuestionWorkMapper.queryQuestionWorkExist(questionWork) > 0) {
            return;
        }
        tbQuestionWorkMapper.insertQuestionWork(questionWork);
    }

    /**
     * 移除错题/收藏
     *
     * @param param
     */
    public void removeQuestionWork(Map<String, Object> param) {
        TbQuestionWork questionWork = new TbQuestionWork();
        questionWork.setUserId(VisitInfoHolder.getUid());
        questionWork.setWorkType(MapUtils.getInteger(param, "workType"));
        questionWork.setSubType(MapUtils.getInteger(param, "subType"));
        questionWork.setQuestionId(MapUtils.getInteger(param, "pkid"));
        questionWork.setQuestionType(MapUtils.getInteger(param,"questionType"));
        tbQuestionWorkMapper.deleteQuestionWork(questionWork);
    }

    private void setQuestion(List<Map<String,Object>> list){
        if (null != list && list.size() > 0){
            String question = "";
            for (Map<String, Object> map : list) {
                if (null != map.get("question")) {
                    question = MapUtils.getString(map, "question");
                    map.put("question", com.alibaba.fastjson.JSONObject.parse(question));
                }
            }
        }
    }
}
