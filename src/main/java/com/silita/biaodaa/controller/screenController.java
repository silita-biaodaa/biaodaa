package com.silita.biaodaa.controller;

import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.service.HighwayService;
import com.silita.biaodaa.service.ShuiliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 筛选
 */
@Controller
@RequestMapping("/screen")
public class screenController extends BaseController{
    @Autowired
    HighwayService highwayService;
    @Autowired
    ShuiliService shuiliService;
    @Autowired
    MyRedisTemplate myRedisTemplate;

    @ResponseBody
    @RequestMapping("/proTypeInBuild")
    public Map<String,Object> proTypeInBuild(){
        String key = "pro_type_in_build";
        Map<String,Object> maps = (Map<String, Object>) myRedisTemplate.getObject(key);
        if(null != maps && maps.size() > 0){
            return successMsgs(maps);
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("gonglu",highwayService.getBuildInProType());
        resultMap.put("shuili",shuiliService.getProType());
        myRedisTemplate.setObject(key,resultMap);
        return successMsgs(resultMap);
    }
}
