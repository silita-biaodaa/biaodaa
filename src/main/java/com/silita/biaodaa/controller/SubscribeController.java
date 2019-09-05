package com.silita.biaodaa.controller;

import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.service.SubscribeService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 订阅
 * Created by zhushuai on 2019/9/2.
 */
@Controller
@RequestMapping("/subscribe")
public class SubscribeController extends BaseController{

    @Autowired
    SubscribeService subscribeService;

    /**
     * 设置订阅条件
     *
     * @param param
     */
    @ResponseBody
    @RequestMapping(value = "/save/condition", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> saveCondition(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashedMap();
        successMsg(result);
        subscribeService.saveSubscribeCondition(VisitInfoHolder.getUid(),param);
        return result;
    }

    /**
     * 获取订阅条件
     *
     * @param param
     */
    @ResponseBody
    @RequestMapping(value = "/gain/condition", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> getCondition(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashedMap();
        successMsg(result,subscribeService.getSubscribeCondition(param));
        return result;
    }
}
