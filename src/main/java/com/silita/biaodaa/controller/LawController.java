package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.LawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/law")
@Controller
public class LawController {

    @Autowired
    LawService lawService;

    /**
     * 法务列表
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> list(@RequestBody Map<String, Object> params) {
        Map<String, Object> resultMap = lawService.getLawList(params);
        resultMap.put("code", 1);
        resultMap.put("msg", "操作成功");
        return resultMap;
    }

    /**
     * 法务详情
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/detail", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> detail(@RequestBody Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 1);
        resultMap.put("msg", "操作成功");
        resultMap.put("data",lawService.getLawDetal(params));
        return resultMap;
    }

    /**
     * 统计公司法务数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/countComLaw", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> countComLaw(@RequestBody Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 1);
        resultMap.put("msg", "操作成功");
        lawService.countCompanyLaw();
        return resultMap;
    }

    /**
     * 统计
     */
    public void countLaw() {

    }
}
