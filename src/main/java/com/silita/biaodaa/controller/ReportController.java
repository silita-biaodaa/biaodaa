package com.silita.biaodaa.controller;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by zhushuai on 2019/4/16.
 */
@Controller
@RequestMapping("report")
public class ReportController extends BaseController{

    /**
     * 综合查询并返回查询条件
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/query",method = RequestMethod.POST)
    public Map<String,Object> queryReport(@RequestBody Map<String,Object> param){
        Map<String,Object> resultMap = new HashedMap();
        successMsg(resultMap);
        return resultMap;
    }
}
