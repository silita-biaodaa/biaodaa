package com.silita.biaodaa.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by dh on 2019/1/9.
 */
@RequestMapping("/permission")
@Controller
public class MemberPermissionController  extends BaseController {

    Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private UnderConstructController underConstructController;

    @ResponseBody
    @RequestMapping(value = "/under/list", produces = "application/json;charset=utf-8")
    public Map<String, Object> underList(@RequestBody Map<String, Object> param){
        return underConstructController.underList(param);
    }

    @ResponseBody
    @RequestMapping(value = "/under/query", produces = "application/json;charset=utf-8")
    public Map<String, Object> underQuery(@RequestBody Map<String, Object> param){
        return underConstructController.underQuery(param);
    }

}
