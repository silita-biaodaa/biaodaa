package com.silita.biaodaa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhushuai on 2019/3/7.
 */
@Controller
@RequestMapping("/api")
public class ApiController {

    @ResponseBody
    @RequestMapping(value = "/list", produces = "application/json;charset=utf-8")
    public Map<String, Object> list(@RequestBody Map<String,Object> param) {
        Map result = new HashMap();
        return result;
    }

}
