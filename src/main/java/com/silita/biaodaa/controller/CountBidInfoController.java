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
     * TODO: 列表
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> listCountBid(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        if (null == param.get("pkid")) {
            resultMap.put("code", this.INVALIDATE_PARAM_CODE);
            resultMap.put("msg", "pkid字段必填！");
            return resultMap;
        }
        successMsg(resultMap, countBidInfoService.listCountBid(param));
        return resultMap;
    }

}
