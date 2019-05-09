package com.silita.biaodaa.controller;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.service.QuestionService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by zhushuai on 2019/5/7.
 */
@Controller
@RequestMapping("/question")
public class QuestionController extends BaseController {

    @Autowired
    QuestionService questionService;

    /**
     * 题目类别
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/type", method = RequestMethod.POST)
    public Map<String, Object> type() {
        Map<String, Object> resultMap = new HashedMap();
        successMsg(resultMap, questionService.getQuestionType());
        return resultMap;
    }

    /**
     * 题目个数
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/count", method = RequestMethod.POST)
    public Map<String, Object> count(@RequestBody Map<String,Object> param) {
        Map<String, Object> resultMap = new HashedMap();
        successMsg(resultMap, questionService.getQuestionCount(param));
        return resultMap;
    }

    /**
     * 题目列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Map<String, Object> list(@RequestBody Map<String,Object> param) {
        Map<String, Object> resultMap = new HashedMap();
        Map<String,Object> dataMap = new HashedMap(2);
        dataMap.put("list",questionService.getListQuestion(param));
        param.put("userId", VisitInfoHolder.getUid());
        param.put("workType",2);
        dataMap.put("collect",questionService.getListQuestionWork(param));
        successMsg(resultMap, dataMap);
        return resultMap;
    }

    /**
     * 错题/收藏列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/work/list", method = RequestMethod.POST)
    public Map<String, Object> listError(@RequestBody Map<String,Object> param) {
        Map<String, Object> resultMap = new HashedMap();
        param.put("userId",VisitInfoHolder.getUid());
        Integer workType = MapUtils.getInteger(param,"workType");
        if (Constant.WORK_TYPE_ERROR.intValue() == workType){
            Map<String,Object> dataMap = new HashedMap(2);
            dataMap.put("list",questionService.getListQuestionWork(param));
            param.put("workType",2);
            dataMap.put("collect",questionService.getListQuestionWork(param));
            successMsg(resultMap, dataMap);
            return resultMap;
        }
        successMsg(resultMap, questionService.getListQuestionWork(param));
        return resultMap;
    }

    /**
     * 添加错题/收藏
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/add/work", method = RequestMethod.POST)
    public Map<String, Object> addWork(@RequestBody Map<String,Object> param) {
        Map<String, Object> resultMap = new HashedMap();
        questionService.addQuestionWork(param);
        successMsg(resultMap);
        return resultMap;
    }

    /**
     * 移除错题/收藏
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/remove/work", method = RequestMethod.POST)
    public Map<String, Object> removeWork(@RequestBody Map<String,Object> param) {
        Map<String, Object> resultMap = new HashedMap();
        questionService.removeQuestionWork(param);
        successMsg(resultMap);
        return resultMap;
    }
}
