package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.CountBidInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/count")
@RestController
public class CountBidInfoController extends BaseController{

    @Autowired
    CountBidInfoService countBidInfoService;

    @RequestMapping(value = "/timer",method = RequestMethod.POST)
    public Map<String,Object> manualTimerCount(){
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("code",this.SUCCESS_CODE);
        resultMap.put("msg",this.SUCCESS_MSG);
//        countBidInfoService.timerCount();
        return resultMap;
    }

    /**
     *列表
     * @param statDate
     * @return
     */
    @RequestMapping(value = "list",method = RequestMethod.POST)
    public Map<String,Object> listCountBid(String statDate){
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("code",this.SUCCESS_CODE);
        resultMap.put("msg",this.SUCCESS_MSG);
        resultMap.put("data",countBidInfoService.listCountBid(statDate));
        return resultMap;
    }
}
