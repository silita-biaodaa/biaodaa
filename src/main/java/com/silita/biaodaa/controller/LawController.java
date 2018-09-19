package com.silita.biaodaa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@RequestMapping("/law")
@Controller
public class LawController {

    /**
     * 法务列表
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json")
    public Map<String,Object> list(@RequestBody Map<String, Object> params){
        return null;
    }

    /**
     * 法务列表年份
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/year", method = RequestMethod.POST, produces = "application/json")
    public Map<String,Object> year(){
        return null;
    }

    /**
     * 统计公司法务数据
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/countComLaw", method = RequestMethod.POST, produces = "application/json")
    public Map<String,Object> countComLaw(@RequestBody Map<String, Object> params){
        return null;
    }

    /**
     * 统计
     */
    public void countLaw(){

    }
}
