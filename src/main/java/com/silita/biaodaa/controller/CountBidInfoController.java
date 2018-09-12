package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.CountBidInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 中标企业统计
 */
@RequestMapping("/count")
@RestController
public class CountBidInfoController extends BaseController {

    @Autowired
    CountBidInfoService countBidInfoService;

    /**
     * TODO: 手动执行统计近一年中标数据
     *
     * @return
     */
    @RequestMapping(value = "/timer", method = RequestMethod.POST)
    public Map<String, Object> manualTimerCount() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", this.SUCCESS_CODE);
        resultMap.put("msg", this.SUCCESS_MSG);
        countBidInfoService.timerCount();
        return resultMap;
    }

    /**
     * TODO: 列表
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> listCountBid(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", this.SUCCESS_CODE);
        resultMap.put("msg", this.SUCCESS_MSG);
        String statDate = null;
        if (null != param.get("statDate")) {
            statDate = param.get("statDate").toString();
        }
        param.put("count",3);
        if(statDate.contains("~")){
            String[] str = statDate.split("~");
            param.put("statDate",str[0]);
            param.put("endDate",str[1]);
            param.put("count",10);
            param.put("ressDate",statDate);
        }
        resultMap.put("data", countBidInfoService.listCountBid(param));
        return resultMap;
    }


    @ResponseBody
    @RequestMapping(value = "/manualCount", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String,Object> manualCount(@RequestBody Map<String, Object> param){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", this.SUCCESS_CODE);
        resultMap.put("msg", this.SUCCESS_MSG);
        countBidInfoService.countDates(param);
        return resultMap;
    }
}
